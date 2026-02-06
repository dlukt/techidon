package de.icod.techidon.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import de.icod.techidon.E;
import de.icod.techidon.R;
import de.icod.techidon.api.requests.lists.DeleteList;
import de.icod.techidon.api.requests.lists.GetListAccounts;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.events.ListDeletedEvent;
import de.icod.techidon.fragments.settings.BaseSettingsFragment;
import de.icod.techidon.model.Account;
import de.icod.techidon.model.FollowList;
import de.icod.techidon.model.HeaderPaginationList;
import de.icod.techidon.model.viewmodel.AvatarPileListItem;
import de.icod.techidon.model.viewmodel.CheckableListItem;
import de.icod.techidon.model.viewmodel.ListItem;
import de.icod.techidon.ui.views.FloatingHintEditTextLayout;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.Nav;
import me.grishka.appkit.api.APIRequest;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.imageloader.requests.UrlImageLoaderRequest;
import me.grishka.appkit.utils.MergeRecyclerAdapter;
import me.grishka.appkit.utils.SingleViewRecyclerAdapter;
import me.grishka.appkit.utils.V;

@SuppressWarnings("deprecation")

public abstract class BaseEditListFragment extends BaseSettingsFragment<Void>{
	private static final String STATE_TITLE="state_title";
	private static final String STATE_REPLIES_POLICY="state_replies_policy";
	private static final String STATE_EXCLUSIVE="state_exclusive";

	protected FollowList followList;
	protected AvatarPileListItem<Void> membersItem;
	protected CheckableListItem<Void> exclusiveItem;
	protected FloatingHintEditTextLayout titleEditLayout;
	protected EditText titleEdit;
	protected Spinner showRepliesSpinner;
	private APIRequest<?> getMembersRequest;
	private String pendingTitle;
	private FollowList.RepliesPolicy pendingRepliesPolicy;
	private Boolean pendingExclusive;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		followList=Parcels.unwrap(getArguments().getParcelable("list"));
		restorePendingState(savedInstanceState);
		if(savedInstanceState!=null && !data.isEmpty() && restoreItemsFromData()){
			rebindRestoredItems();
			applyPendingExclusive();
			return;
		}

		membersItem=new AvatarPileListItem<>(getString(R.string.list_members), null, List.of(), 0, i->onMembersClick(), null, false);
		List<ListItem<Void>> items=new ArrayList<>();
		if(followList!=null){
			items.add(membersItem);
		}
		boolean initialExclusive=followList!=null && followList.exclusive;
		if(pendingExclusive!=null){
			initialExclusive=pendingExclusive;
		}
		exclusiveItem=new CheckableListItem<>(R.string.list_exclusive, R.string.list_exclusive_subtitle, CheckableListItem.Style.SWITCH, initialExclusive, this::toggleCheckableItem);
		items.add(exclusiveItem);
		onDataLoaded(items);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(getMembersRequest!=null)
			getMembersRequest.cancel();
	}

	@Override
	protected void doLoadData(int offset, int count){}

	@Override
	protected RecyclerView.Adapter<?> getAdapter(){
		LinearLayout topView=new LinearLayout(getActivity());
		topView.setOrientation(LinearLayout.VERTICAL);

		titleEditLayout=(FloatingHintEditTextLayout) getActivity().getLayoutInflater().inflate(R.layout.floating_hint_edit_text, topView, false);
		titleEdit=titleEditLayout.findViewById(R.id.edit);
		titleEdit.setHint(R.string.list_name);
		titleEditLayout.updateHint();
		if(pendingTitle!=null){
			titleEdit.setText(pendingTitle);
		}else if(followList!=null){
			titleEdit.setText(followList.title);
		}
		topView.addView(titleEditLayout);

		FloatingHintEditTextLayout showRepliesLayout=(FloatingHintEditTextLayout) getActivity().getLayoutInflater().inflate(R.layout.floating_hint_spinner, topView, false);
		showRepliesSpinner=showRepliesLayout.findViewById(R.id.spinner);
		showRepliesLayout.setHint(R.string.list_show_replies_to);
		topView.addView(showRepliesLayout);
		ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(getActivity(), R.layout.item_spinner, List.of(
				getString(R.string.list_replies_no_one),
				getString(R.string.list_replies_members),
				getString(R.string.list_replies_anyone)
		));
		showRepliesSpinner.setAdapter(spinnerAdapter);
		FollowList.RepliesPolicy repliesPolicy=pendingRepliesPolicy!=null ? pendingRepliesPolicy : (followList!=null ? followList.repliesPolicy : FollowList.RepliesPolicy.LIST);
		showRepliesSpinner.setSelection(switch(repliesPolicy){
			case FOLLOWED -> 2;
			case LIST -> 1;
			case NONE -> 0;
		});
		ViewGroup.MarginLayoutParams llp=(ViewGroup.MarginLayoutParams)showRepliesLayout.getLabel().getLayoutParams();
		llp.setMarginStart(llp.getMarginStart()+V.dp(16));

		MergeRecyclerAdapter adapter=new MergeRecyclerAdapter();
		adapter.addAdapter(new SingleViewRecyclerAdapter(topView));
		adapter.addAdapter(MergeRecyclerAdapter.asViewHolderAdapter(super.getAdapter()));
		return adapter;
	}

	@Override
	protected int indexOfItemsAdapter(){
		return 1;
	}

	protected void doDeleteList(){
		new DeleteList(followList.id)
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(Void result){
						AccountSessionManager.get(accountID).getCacheController().deleteList(followList.id);
						E.post(new ListDeletedEvent(accountID, followList.id));
						Nav.finish(BaseEditListFragment.this);
					}

					@Override
					public void onError(ErrorResponse error){
						Activity activity=getActivity();
						if(activity==null)
							return;
						error.showToast(activity);
					}
				})
				.wrapProgress(getActivity(), R.string.loading, true)
				.exec(accountID);
	}

	private void onMembersClick(){
		Bundle args=new Bundle();
		args.putString("account", accountID);
		args.putParcelable("list", Parcels.wrap(followList));
		Nav.go(getActivity(), ListMembersFragment.class, args);
	}

	protected void loadMembers(){
		getMembersRequest=new GetListAccounts(followList.id, null, 3)
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(HeaderPaginationList<Account> result){
						getMembersRequest=null;
						membersItem.avatars=new ArrayList<>();
						for(int i=0;i<Math.min(3, result.size());i++){
							Account acc=result.get(i);
							membersItem.avatars.add(new UrlImageLoaderRequest(acc.avatarStatic, V.dp(32), V.dp(32)));
						}
						rebindItem(membersItem);
						imgLoader.updateImages();
					}

					@Override
					public void onError(ErrorResponse error){
						getMembersRequest=null;
					}
				})
				.exec(accountID);
	}

	protected FollowList.RepliesPolicy getSelectedRepliesPolicy(){
		return switch(showRepliesSpinner.getSelectedItemPosition()){
			case 0 -> FollowList.RepliesPolicy.NONE;
			case 1 -> FollowList.RepliesPolicy.LIST;
			case 2 -> FollowList.RepliesPolicy.FOLLOWED;
			default -> throw new IllegalStateException("Unexpected value: "+showRepliesSpinner.getSelectedItemPosition());
		};
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if(titleEdit!=null)
			outState.putString(STATE_TITLE, titleEdit.getText().toString());
		if(showRepliesSpinner!=null)
			outState.putString(STATE_REPLIES_POLICY, getSelectedRepliesPolicy().name());
		if(exclusiveItem!=null)
			outState.putBoolean(STATE_EXCLUSIVE, exclusiveItem.checked);
	}

	private void restorePendingState(Bundle savedInstanceState){
		if(savedInstanceState==null)
			return;
		pendingTitle=savedInstanceState.getString(STATE_TITLE);
		String repliesName=savedInstanceState.getString(STATE_REPLIES_POLICY);
		if(repliesName!=null){
			try{
				pendingRepliesPolicy=FollowList.RepliesPolicy.valueOf(repliesName);
			}catch(IllegalArgumentException ignored){
				pendingRepliesPolicy=null;
			}
		}
		if(savedInstanceState.containsKey(STATE_EXCLUSIVE)){
			pendingExclusive=savedInstanceState.getBoolean(STATE_EXCLUSIVE);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean restoreItemsFromData(){
		membersItem=null;
		exclusiveItem=null;
		for(ListItem<Void> item : data){
			if(item instanceof AvatarPileListItem<?> avatarItem){
				membersItem=(AvatarPileListItem<Void>) avatarItem;
			}else if(item instanceof CheckableListItem<?> checkableItem){
				exclusiveItem=(CheckableListItem<Void>) checkableItem;
			}
		}
		return exclusiveItem!=null;
	}

	private void rebindRestoredItems(){
		if(membersItem!=null){
			membersItem.setOnClick((AvatarPileListItem<Void> item)->onMembersClick());
			membersItem.isEnabled=true;
		}
		if(exclusiveItem!=null){
			exclusiveItem.setOnClick((CheckableListItem<Void> item)->toggleCheckableItem(item));
			exclusiveItem.isEnabled=true;
		}
	}

	private void applyPendingExclusive(){
		if(exclusiveItem!=null && pendingExclusive!=null){
			exclusiveItem.checked=pendingExclusive;
		}
	}
}
