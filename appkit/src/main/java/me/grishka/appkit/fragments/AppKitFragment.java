package me.grishka.appkit.fragments;

import androidx.fragment.app.DialogFragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import me.grishka.appkit.R;
import me.grishka.appkit.utils.V;
import me.grishka.appkit.views.FragmentRootLinearLayout;

/**
 * Base class for all your fragments.
 */
public class AppKitFragment extends DialogFragment implements WindowInsetsAwareFragment, MenuProvider {
	private boolean viewCreated;
	private CharSequence title, subtitle;
	private Toolbar toolbar;
	protected boolean hasOptionsMenu;
	private Spinner navigationSpinner;
	protected boolean isTablet;
	protected int scrW;
	private boolean titleMarquee=true, subtitleMarquee=true;
	private FragmentResultCallback resultCallback;
	private View rootView;
	protected TextView toolbarTitleView, toolbarSubtitleView;
	private boolean ignoreSpinnerSelection;
	private boolean resumed;
	private boolean hidden;
	private boolean menuProviderRegistered;

	/**
	 * If your fragment is used as a child in TabbedFragment, this will be in the arguments.
	 * Toolbar, if present, will be removed automatically.
	 */
	public static final String EXTRA_IS_TAB="__is_tab";

	public boolean wantsCustomNavigationIcon(){
		return false;
	}

	public void onToolbarNavigationClick(){
		requireActivity().getOnBackPressedDispatcher().onBackPressed();
	}

	protected boolean canGoBack(){
		return getArguments()!=null && getArguments().getBoolean("_can_go_back");
	}

	public void setTitleMarqueeEnabled(boolean enabled){
		titleMarquee=enabled;
		updateToolbarMarquee();
	}

	public void setSubtitleMarqueeEnabled(boolean enabled){
		subtitleMarquee=enabled;
		updateToolbarMarquee();
	}

	public boolean isTitleMarqueeEnabled(boolean enabled){
		return titleMarquee;
	}

	public boolean isSubitleMarqueeEnabled(boolean enabled){
		return subtitleMarquee;
	}

	private void updateToolbarMarquee(){
		if(toolbar==null){
			return;
		}
		if(toolbarTitleView!=null){
			toolbarTitleView.setFadingEdgeLength(V.dp(10));
			toolbarTitleView.setHorizontalFadingEdgeEnabled(true);
			toolbarTitleView.setMarqueeRepeatLimit(1);
			if(titleMarquee){
				toolbarTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
				toolbarTitleView.setSelected(true);
			}else{
				toolbarTitleView.setSelected(false);
				toolbarTitleView.setEllipsize(TextUtils.TruncateAt.END);
			}
		}
		if(toolbarSubtitleView!=null){
			toolbarSubtitleView.setFadingEdgeLength(V.dp(10));
			toolbarSubtitleView.setHorizontalFadingEdgeEnabled(true);
			toolbarSubtitleView.setMarqueeRepeatLimit(1);
			if(subtitleMarquee){
				toolbarSubtitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
				toolbarSubtitleView.setSelected(true);
			}else{
				toolbarSubtitleView.setSelected(false);
				toolbarSubtitleView.setEllipsize(TextUtils.TruncateAt.END);
			}
		}
	}

	private void initToolbar(){
		toolbar.setTitle("[title]");
		toolbar.setSubtitle("[subtitle]");
		for(int i=0;i<toolbar.getChildCount();i++){
			View child=toolbar.getChildAt(i);
			if(child instanceof TextView){
				TextView textView=(TextView) child;
				String val=textView.getText().toString();
				if("[title]".equals(val)){
					toolbarTitleView=textView;
				}else if("[subtitle]".equals(val)){
					toolbarSubtitleView=textView;
				}
			}
		}

		if(title!=null)
			toolbar.setTitle(title);
		else
			toolbar.setTitle(null);
		if(subtitle!=null)
			toolbar.setSubtitle(subtitle);
		else
			toolbar.setSubtitle(null);
		if(hasOptionsMenu){
			invalidateToolbarMenu();
			toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					return onAppMenuItemSelected(item);
				}
			});
		}
		if(wantsCustomNavigationIcon()){
			Drawable d=getNavigationIconDrawable().mutate();
			TypedArray ta=toolbar.getContext().obtainStyledAttributes(new int[]{android.R.attr.textColorSecondary});
			d.setTint(ta.getColor(0, 0xFF000000));
			ta.recycle();
			toolbar.setNavigationIcon(d);
		}else if(canGoBack()){
			int[] attrs={R.attr.appkitBackDrawable, android.R.attr.textColorSecondary};
			TypedArray ta=toolbar.getContext().obtainStyledAttributes(attrs);
			Drawable d=ta.getDrawable(0);
			int tintColor=ta.getColor(1, 0xFF000000);
			ta.recycle();
			if(d==null)
				d=getResources().getDrawable(R.drawable.ic_arrow_back, toolbar.getContext().getTheme());
			d=d.mutate();
			d.setTint(tintColor);
			toolbar.setNavigationIcon(d);
		}
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onToolbarNavigationClick();
			}
		});
		if(navigationSpinner!=null){
			toolbar.addView(navigationSpinner, new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
			toolbar.setTitle(null);
			toolbar.setSubtitle(null);
		}

		onUpdateToolbar();
	}

	protected void onUpdateToolbar(){}

	protected Drawable getNavigationIconDrawable(){
		return getResources().getDrawable(getNavigationIconDrawableResource(), getActivity().getTheme());
	}

	@DrawableRes
	protected int getNavigationIconDrawableResource(){
		return R.drawable.ic_arrow_back;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		rootView=view;

		Window window=getActivity()!=null ? getActivity().getWindow() : null;
		if(window!=null){
			setStatusBarColor(getWindowStatusBarColor(window));
			setNavigationBarColor(getWindowNavigationBarColor(window));
		}

		toolbar=(Toolbar)view.findViewById(R.id.toolbar);
		if(toolbar!=null && getArguments()!=null && getArguments().getBoolean(EXTRA_IS_TAB)){
			((ViewGroup)toolbar.getParent()).removeView(toolbar);
			toolbar=null;
		}
		viewCreated=true;
		if(toolbar!=null){
			initToolbar();
		}else{
			if(title!=null){
				if(getArguments()==null || !getArguments().getBoolean("_dialog"))
					getActivity().setTitle(title);
			}
			if(getActivity().getActionBar()!=null && (getArguments()==null || !getArguments().getBoolean("_dialog"))) {
				if(title!=null){
					getActivity().getActionBar().setDisplayShowTitleEnabled(true);
				}
				if(subtitle!=null){
					getActivity().getActionBar().setSubtitle(subtitle);
				}
			}
		}
		updateToolbarMarquee();
		updateMenuProvider();
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		removeMenuProvider();
		navigationSpinner=null;
		toolbar=null;
	}

	public void setHasOptionsMenuCompat(boolean hasMenu){
		hasOptionsMenu=hasMenu;
		updateMenuProvider();
		invalidateOptionsMenu();
	}

	public void invalidateOptionsMenu(){
		if(toolbar!=null){
			invalidateToolbarMenu();
		}else if(getActivity() instanceof MenuHost){
			MenuHost host=(MenuHost) getActivity();
			updateMenuProvider();
			host.invalidateMenu();
		}
	}

	private void updateMenuProvider(){
		if(getView()==null)
			return;
		if(toolbar!=null){
			removeMenuProvider();
			return;
		}
		if(!(getActivity() instanceof MenuHost))
			return;
		MenuHost host=(MenuHost) getActivity();
		if(hasOptionsMenu){
			if(!menuProviderRegistered){
				host.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
				menuProviderRegistered=true;
			}
		}else{
			removeMenuProvider();
		}
	}

	private void removeMenuProvider(){
		if(menuProviderRegistered && getActivity() instanceof MenuHost){
			((MenuHost) getActivity()).removeMenuProvider(this);
		}
		menuProviderRegistered=false;
	}

	@Override
	public void onCreateMenu(Menu menu, MenuInflater menuInflater){
		onCreateAppMenu(menu, menuInflater);
	}

	@Override
	public void onPrepareMenu(Menu menu){
		onPrepareAppMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(MenuItem menuItem){
		return onAppMenuItemSelected(menuItem);
	}

	private void invalidateToolbarMenu(){
		toolbar.getMenu().clear();
		if(hasOptionsMenu){
			Menu menu=toolbar.getMenu();
			onCreateAppMenu(menu, new MenuInflater(getActivity()));
			if(wantsToolbarMenuIconsTinted()){
				TypedArray ta=toolbar.getContext().obtainStyledAttributes(new int[]{R.attr.actionBarIconTint});
				ColorStateList tintColor=ta.getColorStateList(0);
				ta.recycle();
				if(tintColor==null)
					return;
				for(int i=0;i<menu.size();i++){
					MenuItem item=menu.getItem(i);
					if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
						Drawable icon=item.getIcon();
						if(icon!=null && icon.getColorFilter()==null){
							icon=icon.mutate();
							icon.setTintList(tintColor);
							item.setIcon(icon);
						}
					}else{
						item.setIconTintList(tintColor);
					}
				}
			}
		}
	}

	protected boolean wantsToolbarMenuIconsTinted(){
		return true;
	}

	protected Toolbar getToolbar(){
		return toolbar;
	}

	@Override
	public void onAttach(Context activity){
		super.onAttach(activity);
		V.setApplicationContext(activity);
		updateConfiguration();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		updateConfiguration();
		if(toolbar!=null){
			ViewGroup parent=(ViewGroup) toolbar.getParent();
			int index=parent.indexOfChild(toolbar);
			parent.removeView(toolbar);
			if(navigationSpinner!=null){
				toolbar.removeView(navigationSpinner);
				ignoreSpinnerSelection=true;
				int selectedItem=navigationSpinner.getSelectedItemPosition();
				SpinnerAdapter adapter=navigationSpinner.getAdapter();
				navigationSpinner.setAdapter(null);
				navigationSpinner.setAdapter(adapter);
				navigationSpinner.setSelection(selectedItem);
				ignoreSpinnerSelection=false;
			}
			toolbar=(Toolbar)getToolbarLayoutInflater().inflate(getToolbarResource(), parent, false);
			parent.addView(toolbar, index);
			initToolbar();
			updateToolbarMarquee();
		}
		updateMenuProvider();
	}

	protected LayoutInflater getToolbarLayoutInflater(){
		return LayoutInflater.from(getActivity());
	}

	@LayoutRes
	protected int getToolbarResource(){
		return R.layout.appkit_toolbar;
	}


	private void updateConfiguration() {
		scrW = getResources().getConfiguration().screenWidthDp;
		isTablet = scrW >= 924;
	}


	protected void setTitle(CharSequence title){
		this.title=title;
		if(navigationSpinner!=null)
			return;
		if(toolbar!=null) {
			toolbar.setTitle(title);
			updateToolbarMarquee();
		}
	}

	protected void setTitle(int res){
		setTitle(getString(res));
	}

	public CharSequence getTitle(){
		return title;
	}

	protected void setSubtitle(CharSequence subtitle){
		this.subtitle=subtitle;
		if(navigationSpinner!=null)
			return;
		if(toolbar!=null){
			toolbar.setSubtitle(subtitle);
			updateToolbarMarquee();
		}else if(viewCreated && getActivity().getActionBar()!=null){
			getActivity().getActionBar().setSubtitle(subtitle);
		}
	}

	protected void setSubtitle(int res){
		setSubtitle(getString(res));
	}

	protected ArrayAdapter<Object> onCreateNavigationSpinnerAdapter(){
		return new NavigationSpinnerAdapter(getActivity());
	}

	protected void setSpinnerItems(List<?> items){
		if(items==null){
			setSpinnerAdapter(null);
			return;
		}
		ArrayAdapter<Object> adapter = onCreateNavigationSpinnerAdapter();
		adapter.addAll(items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setSpinnerAdapter(adapter);
	}

	protected Spinner onCreateNavigationSpinner(LayoutInflater inflater){
		return (Spinner) inflater.inflate(R.layout.appkit_navigation_spinner, null);
	}

	protected void setSpinnerAdapter(SpinnerAdapter adapter){
		if(adapter==null){
			if(navigationSpinner!=null){
				toolbar.removeView(navigationSpinner);
				navigationSpinner=null;
			}
			return;
		}
		if(navigationSpinner==null){
			navigationSpinner=onCreateNavigationSpinner(getActivity().getLayoutInflater());
			navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
					if(!ignoreSpinnerSelection)
						onSpinnerItemSelected(pos);
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {

				}
			});
			if(toolbar!=null){
				toolbar.addView(navigationSpinner, new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
				toolbar.setTitle(null);
				toolbar.setSubtitle(null);
			}
		}
		navigationSpinner.setAdapter(adapter);
	}

	protected Context getToolbarContext(){
		TypedArray ta=getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarTheme});
		int themeID=ta.getResourceId(0, 0);
		ta.recycle();
		if(themeID==0){
			return getActivity();
		}
		return new ContextThemeWrapper(getActivity(), themeID);
	}

	protected Context getToolbarPopupContext(){
		TypedArray ta=getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarPopupTheme});
		int themeID=ta.getResourceId(0, 0);
		ta.recycle();
		if(themeID==0){
			return getActivity();
		}
		return new ContextThemeWrapper(getActivity(), themeID);
	}

	/**
	 * Override this to get notified when the user selects an item in the toolbar spinner.
	 * @param position The position of the selected item
	 * @return True if the event is handled and the item should remain selected, false otherwise
	 */
	protected boolean onSpinnerItemSelected(int position){
		return false;
	}

	protected void setSelectedNavigationItem(int position){
		navigationSpinner.setSelection(position);
	}

	protected int getSelectedNavigationItem(){
		if(navigationSpinner==null)
			return -1;
		return navigationSpinner.getSelectedItemPosition();
	}

	public void setResultCallback(FragmentResultCallback resultCallback){
		this.resultCallback=resultCallback;
	}

	protected void setResult(boolean success, Bundle result){
		if(resultCallback!=null)
			resultCallback.onFragmentResult(success, result);
	}

	public void onFragmentResult(int reqCode, boolean success, Bundle result){

	}

	public void onTransitionFinished(){}

	@Override
	public void onApplyWindowInsets(WindowInsets insets){
		if(rootView!=null)
			rootView.dispatchApplyWindowInsets(insets);
	}

	@Override
	public boolean wantsLightStatusBar(){
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && rootView!=null){
			TypedArray ta=rootView.getContext().obtainStyledAttributes(new int[]{android.R.attr.windowLightStatusBar});
			boolean light=ta.getBoolean(0, false);
			ta.recycle();
			return light;
		}
		return false;
	}

	@Override
	public boolean wantsLightNavigationBar(){
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O_MR1 && rootView!=null){
			TypedArray ta=rootView.getContext().obtainStyledAttributes(new int[]{android.R.attr.windowLightNavigationBar});
			boolean light=ta.getBoolean(0, false);
			ta.recycle();
			return light;
		}
		return false;
	}

	protected void setStatusBarColor(int color){
		if(rootView instanceof FragmentRootLinearLayout)
			((FragmentRootLinearLayout) rootView).setStatusBarColor(color);
	}

	protected void setNavigationBarColor(int color){
		if(rootView instanceof FragmentRootLinearLayout)
			((FragmentRootLinearLayout) rootView).setNavigationBarColor(color);
	}

	@Override
	public void onHiddenChanged(boolean hidden){
		super.onHiddenChanged(hidden);
		if(hidden!=this.hidden && resumed){
			this.hidden=hidden;
			if(hidden)
				onHidden();
			else
				onShown();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		resumed=true;
		if(!hidden)
			onShown();
	}

	@Override
	public void onPause(){
		super.onPause();
		resumed=false;
		if(!hidden)
			onHidden();
	}

	protected void onShown(){

	}

	protected void onHidden(){

	}

	@SuppressWarnings("deprecation")
	private static int getWindowStatusBarColor(Window window){
		return window.getStatusBarColor();
	}

	@SuppressWarnings("deprecation")
	private static int getWindowNavigationBarColor(Window window){
		return window.getNavigationBarColor();
	}

	protected void onCreateAppMenu(Menu menu, MenuInflater inflater){}

	protected void onPrepareAppMenu(Menu menu){}

	protected boolean onAppMenuItemSelected(MenuItem item){
		return false;
	}

	protected class NavigationSpinnerAdapter extends ArrayAdapter<Object> {

		public NavigationSpinnerAdapter(Context context) {
			super(context, R.layout.appkit_spinner_view, android.R.id.text1);
			setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			View view=super.getDropDownView(position, convertView, parent);
			if(convertView==null){
				// WTF doesn't it work via XML?
				TypedArray ta=getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorAccent, android.R.attr.colorForeground});
				int colorAccent=ta.getColor(0, 0xFF000000);
				int colorForeground=ta.getColor(1, 0xFF000000);
				ta.recycle();
				ColorStateList csl=new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{colorAccent, colorForeground});
				((TextView) view).setTextColor(csl);
			}
			return view;
		}
	}
}
