package de.icod.techidon.fragments.onboarding;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

import de.icod.techidon.R;
import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.requests.accounts.GetFollowSuggestions;
import de.icod.techidon.api.requests.accounts.SetAccountFollowed;
import de.icod.techidon.fragments.account_list.BaseAccountListFragment;
import de.icod.techidon.model.FollowSuggestion;
import de.icod.techidon.model.Relationship;
import de.icod.techidon.model.viewmodel.AccountViewModel;
import de.icod.techidon.ui.OutlineProviders;
import de.icod.techidon.ui.utils.UiUtils;
import de.icod.techidon.ui.viewholders.AccountViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.Nav;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.api.SimpleCallback;
import me.grishka.appkit.utils.MergeRecyclerAdapter;
import me.grishka.appkit.utils.SingleViewRecyclerAdapter;
import me.grishka.appkit.utils.V;

@SuppressWarnings("deprecation")

public class OnboardingFollowSuggestionsFragment extends BaseAccountListFragment{
	private static final String STATE_PENDING_FOLLOWS="state_pending_follows";
	private static final String STATE_FOLLOW_TOTAL="state_follow_total";
	private static final String STATE_FOLLOW_IN_PROGRESS="state_follow_in_progress";

	private String accountID;
	private View buttonBar;
	private int numRunningFollowRequests=0;
	private ArrayList<String> pendingFollowAccountIds;
	private final ArrayList<String> runningFollowAccountIds=new ArrayList<>();
	private int followTotalCount;
	private boolean followAllInProgress;
	private ProgressDialog followProgress;
	private final ArrayList<MastodonAPIRequest<?>> runningFollowRequests=new ArrayList<>();

	public OnboardingFollowSuggestionsFragment(){
		super(R.layout.fragment_onboarding_follow_suggestions, 40);
		itemLayoutRes=R.layout.item_account_list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			pendingFollowAccountIds=savedInstanceState.getStringArrayList(STATE_PENDING_FOLLOWS);
			followTotalCount=savedInstanceState.getInt(STATE_FOLLOW_TOTAL, 0);
			followAllInProgress=savedInstanceState.getBoolean(STATE_FOLLOW_IN_PROGRESS, false);
			if(!loaded && dataLoading){
				dataLoading=false;
			}
		}
		setTitle(R.string.onboarding_recommendations_title);
		accountID=getArguments().getString("account");
		if(!loaded && !dataLoading)
			loadData();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		buttonBar=view.findViewById(R.id.button_bar);

		view.findViewById(R.id.btn_next).setOnClickListener(UiUtils.rateLimitedClickListener(this::onFollowAllClick));
//		view.findViewById(R.id.btn_skip).setOnClickListener(UiUtils.rateLimitedClickListener(v->proceed()));
		if(followAllInProgress && pendingFollowAccountIds!=null && !pendingFollowAccountIds.isEmpty()){
			showFollowProgress();
			startFollowRequests();
		}
	}

	@Override
	protected void onUpdateToolbar(){
		super.onUpdateToolbar();
		getToolbar().setContentInsetsRelative(V.dp(56), 0);
	}

	@Override
	protected void doLoadData(int offset, int count){
		new GetFollowSuggestions(40)
				.setCallback(new SimpleCallback<>(this){
					@Override
					public void onSuccess(List<FollowSuggestion> result){
						onDataLoaded(result.stream().map(fs->new AccountViewModel(fs.account, accountID).stripLinksFromBio()).collect(Collectors.toList()), false);
					}
				})
				.exec(accountID);
	}

	@Override
	public void onApplyWindowInsets(WindowInsets insets){
		super.onApplyWindowInsets(UiUtils.applyBottomInsetToFixedView(buttonBar, insets));
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if(followAllInProgress && pendingFollowAccountIds!=null){
			ArrayList<String> pendingToSave=new ArrayList<>(pendingFollowAccountIds);
			for(String id : runningFollowAccountIds){
				if(!pendingToSave.contains(id))
					pendingToSave.add(id);
			}
			outState.putStringArrayList(STATE_PENDING_FOLLOWS, pendingToSave);
			outState.putInt(STATE_FOLLOW_TOTAL, followTotalCount);
			outState.putBoolean(STATE_FOLLOW_IN_PROGRESS, true);
		}
	}

	@Override
	public void onDestroy(){
		followAllInProgress=false;
		pendingFollowAccountIds=null;
		for(MastodonAPIRequest<?> req : runningFollowRequests){
			req.cancel();
		}
		runningFollowRequests.clear();
		if(followProgress!=null){
			followProgress.dismiss();
			followProgress=null;
		}
		numRunningFollowRequests=0;
		super.onDestroy();
	}

	@Override
	protected RecyclerView.Adapter<?> getAdapter(){
//		Unused in Techidon
//		TextView introText=new TextView(getActivity());
//		introText.setTextAppearance(R.style.m3_body_large);
//		introText.setTextColor(UiUtils.getThemeColor(getActivity(), R.attr.colorM3OnSurface));
//		introText.setPaddingRelative(V.dp(56), 0, V.dp(24), V.dp(8));
//		introText.setText(R.string.onboarding_recommendations_intro);
		MergeRecyclerAdapter mergeAdapter=new MergeRecyclerAdapter();
//		mergeAdapter.addAdapter(new SingleViewRecyclerAdapter(introText));
		mergeAdapter.addAdapter(MergeRecyclerAdapter.asViewHolderAdapter(super.getAdapter()));
		return mergeAdapter;
	}

	private void onFollowAllClick(View v){
		if(!loaded || relationships.isEmpty())
			return;
		if(data.isEmpty()){
			proceed();
			return;
		}
		if(followAllInProgress)
			return;
		ArrayList<String> accountIdsToFollow=new ArrayList<>();
		for(AccountViewModel acc:data){
			Relationship rel=relationships.get(acc.account.id);
			if(rel==null)
				continue;
			if(rel.canFollow())
				accountIdsToFollow.add(acc.account.id);
		}
		if(accountIdsToFollow.isEmpty())
			return;
		pendingFollowAccountIds=accountIdsToFollow;
		followTotalCount=accountIdsToFollow.size();
		followAllInProgress=true;
		showFollowProgress();
		startFollowRequests();
	}

	private void startFollowRequests(){
		if(pendingFollowAccountIds==null)
			return;
		int toStart=Math.min(pendingFollowAccountIds.size(), 5);
		for(int i=0;i<toStart;i++){
			followNextAccount();
		}
	}

	private void followNextAccount(){
		if(pendingFollowAccountIds==null || pendingFollowAccountIds.isEmpty()){
			if(numRunningFollowRequests==0){
				finishFollowAll();
			}
			return;
		}
		numRunningFollowRequests++;
		String id=pendingFollowAccountIds.remove(0);
		runningFollowAccountIds.add(id);
		SetAccountFollowed req=new SetAccountFollowed(id, true, true);
		runningFollowRequests.add(req);
		req
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(Relationship result){
						runningFollowRequests.remove(req);
						runningFollowAccountIds.remove(id);
						relationships.put(id, result);
						for(int i=0;i<list.getChildCount();i++){
							if(list.getChildViewHolder(list.getChildAt(i)) instanceof AccountViewHolder svh && svh.getItem().account.id.equals(id)){
								svh.rebind();
								break;
							}
						}
						numRunningFollowRequests--;
						updateFollowProgress();
						followNextAccount();
					}

					@Override
					public void onError(ErrorResponse error){
						runningFollowRequests.remove(req);
						runningFollowAccountIds.remove(id);
						numRunningFollowRequests--;
						updateFollowProgress();
						followNextAccount();
					}
				})
				.exec(accountID);
	}

	private void showFollowProgress(){
		if(getActivity()==null || followProgress!=null)
			return;
		followProgress=new ProgressDialog(getActivity());
		followProgress.setIndeterminate(false);
		followProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		followProgress.setMax(followTotalCount);
		followProgress.setCancelable(false);
		followProgress.setMessage(getString(R.string.sending_follows));
		followProgress.show();
		updateFollowProgress();
	}

	private void updateFollowProgress(){
		if(followProgress==null || pendingFollowAccountIds==null)
			return;
		int completed=followProgress.getMax()-pendingFollowAccountIds.size()-numRunningFollowRequests;
		followProgress.setProgress(Math.max(0, completed));
	}

	private void finishFollowAll(){
		followAllInProgress=false;
		pendingFollowAccountIds=null;
		numRunningFollowRequests=0;
		if(followProgress!=null){
			followProgress.dismiss();
			followProgress=null;
		}
		proceed();
	}

	private void proceed(){
//		Bundle args=new Bundle();
//		args.putString("account", accountID);
//		Nav.go(getActivity(), OnboardingProfileSetupFragment.class, args);
	}

	@Override
	protected void onConfigureViewHolder(AccountViewHolder holder){
		super.onConfigureViewHolder(holder);
		holder.setStyle(AccountViewHolder.AccessoryType.BUTTON, true);
		holder.avatar.setOutlineProvider(OutlineProviders.roundedRect(8));
	}

	@Override
	public Uri getWebUri(Uri.Builder base){
		return null;
	}
}
