package de.icod.techidon.fragments.settings;

import android.content.Context;
import android.app.Activity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;

import de.icod.techidon.R;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.fragments.CustomLocalTimelineFragment;
import de.icod.techidon.model.Instance;
import de.icod.techidon.ui.SimpleViewHolder;
import de.icod.techidon.ui.tabs.TabLayout;
import de.icod.techidon.ui.tabs.TabLayoutMediator;
import de.icod.techidon.ui.utils.UiUtils;
import de.icod.techidon.ui.views.NestedRecyclerScrollView;
import org.parceler.Parcels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.AppKitFragment;
import me.grishka.appkit.utils.V;

@SuppressWarnings("deprecation")

public class SettingsServerFragment extends AppKitFragment{
	private String accountID;
	private Instance instance;
	private TabLayout tabBar;
	private TabLayoutMediator tabLayoutMediator;
	private ViewPager2 pager;
	private FrameLayout[] tabViews;
	private View contentView;
	private WindowInsets childInsets;

	private SettingsServerAboutFragment aboutFragment;
	private SettingsServerRulesFragment rulesFragment;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		accountID=getArguments().getString("account");
		instance=getArguments().containsKey("instance")
				? Parcels.unwrap(getArguments().getParcelable("instance"))
				: AccountSessionManager.getOptional(accountID)
					.map(i->AccountSessionManager.getInstance().getInstanceInfo(i.domain))
					.orElseThrow();
		setTitle(instance.title);

		Bundle args=new Bundle();
		args.putString("account", accountID);
		args.putBoolean("__is_tab", true);
		args.putParcelable("instance", Parcels.wrap(instance));
		if(savedInstanceState!=null){
			aboutFragment=getRestoredChildFragment(savedInstanceState, "aboutFragment", SettingsServerAboutFragment.class);
			rulesFragment=getRestoredChildFragment(savedInstanceState, "rulesFragment", SettingsServerRulesFragment.class);
		}
		if(aboutFragment==null){
			aboutFragment=new SettingsServerAboutFragment();
			aboutFragment.setArguments(args);
		}
		if(rulesFragment==null){
			rulesFragment=new SettingsServerRulesFragment();
			rulesFragment.setArguments(args);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if(aboutFragment!=null && aboutFragment.isAdded())
			getChildFragmentManager().putFragment(outState, "aboutFragment", aboutFragment);
		if(rulesFragment!=null && rulesFragment.isAdded())
			getChildFragmentManager().putFragment(outState, "rulesFragment", rulesFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.fragment_settings_server, container, false);

		TextView realTitle=view.findViewById(R.id.real_title);
		realTitle.setText(getTitle());
		realTitle.setSelected(true);

		pager=view.findViewById(R.id.pager);
		pager.setAdapter(new ServerPagerAdapter());

		FrameLayout sizeWrapper=new FrameLayout(getActivity()){
			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
				pager.getLayoutParams().height=MeasureSpec.getSize(heightMeasureSpec)-getPaddingTop()-getPaddingBottom()-V.dp(48);
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		};

		tabViews=new FrameLayout[2];
		for(int i=0;i<tabViews.length;i++){
			FrameLayout tabView=new FrameLayout(getActivity());
			tabView.setId(switch(i){
				case 0 -> R.id.server_about;
				case 1 -> R.id.server_rules;
				default -> throw new IllegalStateException("Unexpected value: "+i);
			});
			tabView.setVisibility(View.GONE);
			sizeWrapper.addView(tabView); // needed so the fragment manager will have somewhere to restore the tab fragment
			tabViews[i]=tabView;
		}

		tabBar=view.findViewById(R.id.tabbar);
		tabBar.setTabTextColors(UiUtils.getThemeColor(getActivity(), R.attr.colorM3OnSurfaceVariant), UiUtils.getThemeColor(getActivity(), R.attr.colorM3Primary));
		tabBar.setTabTextSize(V.dp(14));
		tabLayoutMediator=new TabLayoutMediator(tabBar, pager, (tab, position)->tab.setText(switch(position){
			case 0 -> R.string.about_server;
			case 1 -> R.string.server_rules;
			default -> throw new IllegalStateException("Unexpected value: "+position);
		}));
		tabLayoutMediator.attach();

		NestedRecyclerScrollView scrollView=view.findViewById(R.id.scroller);
		scrollView.setScrollableChildSupplier(()->switch(pager.getCurrentItem()){
			case 0 -> aboutFragment.scroller;
			case 1 -> rulesFragment.getList();
			default -> throw new IllegalStateException("Unexpected value: "+pager.getCurrentItem());
		});

		return contentView=view;
	}

	@Override
	protected void onUpdateToolbar(){
		super.onUpdateToolbar();
		getToolbar().setTitle(null);
	}

	private Fragment getFragmentForPage(int page){
		return switch(page){
			case 0 -> aboutFragment;
			case 1 -> rulesFragment;
			default -> throw new IllegalStateException();
		};
	}

	@Override
	public void onCreateAppMenu(Menu menu, MenuInflater inflater){
		if (instance != null) {
			inflater.inflate(R.menu.instance_info, menu);
			UiUtils.enableOptionsMenuIcons(getActivity(), menu);
			menu.findItem(R.id.share).setTitle(R.string.button_share);

		}
	}

	@Override
	public boolean onAppMenuItemSelected(MenuItem item){
		int id=item.getItemId();
		if(id==R.id.share){
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, instance.normalizedUri);
			startActivity(Intent.createChooser(intent, item.getTitle()));
		} else if (id==R.id.open_timeline) {
			Bundle args=new Bundle();
			args.putString("account", accountID);
			args.putString("domain", instance.normalizedUri);
			Nav.go(getActivity(), CustomLocalTimelineFragment.class, args);
		} else if (id==R.id.open_in_browser){
			UiUtils.launchWebBrowser(getActivity(), new Uri.Builder().scheme("https").authority(instance.uri).appendPath("about").build().toString());
		}
		return true;
	}

	@Override
	public void onAttach(Context activity){
		super.onAttach(activity);
		setHasOptionsMenuCompat(true);
	}

	@Override
	public void onApplyWindowInsets(WindowInsets insets){
		if(contentView!=null){
			if(Build.VERSION.SDK_INT>=29 && insets.getTappableElementInsets().bottom==0){
				int insetBottom=insets.getSystemWindowInsetBottom();
				childInsets=insets.inset(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), 0);
				applyChildWindowInsets();
				insets=insets.inset(0, 0, 0, insetBottom);
			}
		}
		super.onApplyWindowInsets(insets);
	}

	private void applyChildWindowInsets(){
		if(childInsets==null)
			return;
		if(aboutFragment!=null && aboutFragment.isAdded()){
			aboutFragment.onApplyWindowInsets(childInsets);
		}
		if(rulesFragment!=null && rulesFragment.isAdded()){
			rulesFragment.onApplyWindowInsets(childInsets);
		}
	}

	private <T extends Fragment> T getRestoredChildFragment(Bundle state, String key, Class<T> fragmentClass){
		Fragment restored=null;
		if(state.containsKey(key)){
			try{
				restored=getChildFragmentManager().getFragment(state, key);
			}catch(Exception ignored){}
		}
		if(fragmentClass.isInstance(restored))
			return fragmentClass.cast(restored);
		for(Fragment fragment:getChildFragmentManager().getFragments()){
			if(fragmentClass.isInstance(fragment))
				return fragmentClass.cast(fragment);
		}
		return null;
	}

	private class ServerPagerAdapter extends RecyclerView.Adapter<SimpleViewHolder>{
		@NonNull
		@Override
		public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
			FrameLayout view=new FrameLayout(parent.getContext());
			view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			return new SimpleViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position){
			FrameLayout view=tabViews[position];
			if(view.getParent() instanceof ViewGroup parent)
				parent.removeView(view);
			view.setVisibility(View.VISIBLE);
			((FrameLayout)holder.itemView).addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			Fragment fragment=getFragmentForPage(position);
			if(!fragment.isAdded()){
				getChildFragmentManager().beginTransaction().add(view.getId(), fragment).commit();
				holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
					@Override
					public boolean onPreDraw(){
						getChildFragmentManager().executePendingTransactions();
						if(fragment.isAdded()){
							holder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
							applyChildWindowInsets();
						}
						return true;
					}
				});
			}
		}

		@Override
		public int getItemCount(){
			return 2;
		}

		@Override
		public int getItemViewType(int position){
			return position;
		}
	}
}
