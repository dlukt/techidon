package de.icod.techidon.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageButton;

import com.squareup.otto.Subscribe;

import de.icod.techidon.E;
import de.icod.techidon.R;
import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.api.requests.lists.AddAccountsToList;
import de.icod.techidon.api.requests.lists.GetListAccounts;
import de.icod.techidon.api.requests.lists.RemoveAccountsFromList;
import de.icod.techidon.events.AccountAddedToListEvent;
import de.icod.techidon.events.AccountRemovedFromListEvent;
import de.icod.techidon.fragments.account_list.AddListMembersFragment;
import de.icod.techidon.fragments.account_list.PaginatedAccountListFragment;
import de.icod.techidon.model.Account;
import de.icod.techidon.model.FollowList;
import de.icod.techidon.model.viewmodel.AccountViewModel;
import de.icod.techidon.ui.M3AlertDialogBuilder;
import de.icod.techidon.ui.utils.ActionModeHelper;
import de.icod.techidon.ui.utils.UiUtils;
import de.icod.techidon.ui.viewholders.AccountViewHolder;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import me.grishka.appkit.Nav;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.utils.V;

@SuppressWarnings({"deprecation", "unchecked"})

public class ListMembersFragment extends PaginatedAccountListFragment{
	private static final String STATE_SELECTED_ACCOUNTS="selected_accounts";
	private static final String STATE_IN_SELECTION_MODE="in_selection_mode";

	private static final int ADD_MEMBER_RESULT=600;

	private ImageButton fab;
	private FollowList followList;
	private boolean inSelectionMode;
	private Set<String> selectedAccounts=new HashSet<>();
	private ActionMode actionMode;
	private MenuItem deleteItem;
	private boolean restoreSelectionMode;

	public ListMembersFragment(){
		setListLayoutId(R.layout.recycler_fragment_with_fab);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		followList=Parcels.unwrap(getArguments().getParcelable("list"));
		setTitle(R.string.list_members);
		setHasOptionsMenuCompat(true);
		E.register(this);
		if(savedInstanceState!=null){
			ArrayList<String> restored=savedInstanceState.getStringArrayList(STATE_SELECTED_ACCOUNTS);
			if(restored!=null){
				selectedAccounts.clear();
				selectedAccounts.addAll(restored);
			}
			inSelectionMode=savedInstanceState.getBoolean(STATE_IN_SELECTION_MODE, false);
			restoreSelectionMode=inSelectionMode;
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		E.unregister(this);
	}

	@Override
	public HeaderPaginationRequest<Account> onCreateRequest(String maxID, int count){
		return new GetListAccounts(followList.id, maxID, count);
	}

	@Override
	protected MastodonAPIRequest loadRemoteInfo(){
		return null;
	}

	@Override
	public Object getCurrentInfo(){
		return null;
	}

	@Override
	public String getRemoteDomain(){
		return null;
	}

	@Override
	protected void onConfigureViewHolder(AccountViewHolder holder){
		super.onConfigureViewHolder(holder);
		holder.setStyle(inSelectionMode ? AccountViewHolder.AccessoryType.CHECKBOX : AccountViewHolder.AccessoryType.MENU, false);
		holder.setOnClickListener(this::onItemClick);
		holder.setOnLongClickListener(this::onItemLongClick);
		holder.getContextMenu().getMenu().add(0, R.id.remove_from_list, 0, R.string.remove_from_list);
		holder.setOnCustomMenuItemSelectedListener(item->onItemMenuItemSelected(holder, item));
	}

	@Override
	protected void onBindViewHolder(AccountViewHolder holder){
		super.onBindViewHolder(holder);
		holder.setStyle(inSelectionMode ? AccountViewHolder.AccessoryType.CHECKBOX : AccountViewHolder.AccessoryType.MENU, false);
		if(inSelectionMode){
			holder.setChecked(selectedAccounts.contains(holder.getItem().account.id));
		}
	}

	@Override
	public boolean wantsLightStatusBar(){
		if(actionMode!=null)
			return UiUtils.isDarkTheme();
		return super.wantsLightStatusBar();
	}

	@Override
	public void onCreateAppMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.selectable_list, menu);
	}

	@Override
	public boolean onAppMenuItemSelected(MenuItem item){
		int id=item.getItemId();
		if(id==R.id.select){
			enterSelectionMode();
		}else if(id==R.id.select_all){
			for(AccountViewModel a:(ArrayList<AccountViewModel>)data){
				selectedAccounts.add(a.account.id);
			}
			enterSelectionMode();
		}
		return true;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		fab=view.findViewById(R.id.fab);
		fab.setImageResource(R.drawable.ic_fluent_add_24_regular);
		fab.setContentDescription(getString(R.string.add_list_member));
		fab.setOnClickListener(v->onFabClick());
		if(restoreSelectionMode){
			enterSelectionMode();
			if(deleteItem!=null)
				deleteItem.setEnabled(!selectedAccounts.isEmpty());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(STATE_SELECTED_ACCOUNTS, new ArrayList<>(selectedAccounts));
		outState.putBoolean(STATE_IN_SELECTION_MODE, inSelectionMode);
	}

	@Override
	public void onApplyWindowInsets(WindowInsets insets){
		super.onApplyWindowInsets(insets);
		UiUtils.applyBottomInsetToFAB(fab, insets);
	}

	@Override
	public void onFragmentResult(int reqCode, boolean success, Bundle result){
		if(reqCode==ADD_MEMBER_RESULT && success){
			Account acc=Objects.requireNonNull(Parcels.unwrap(result.getParcelable("selectedAccount")));
			addAccounts(List.of(acc));
		}
	}

	@Subscribe
	public void onAccountRemovedFromList(AccountRemovedFromListEvent ev){
		if(ev.accountID.equals(accountID) && ev.listID.equals(followList.id)){
			removeAccountRows(Set.of(ev.targetAccountID));
		}
	}

	@Subscribe
	public void onAccountAddedToList(AccountAddedToListEvent ev){
		if(ev.accountID.equals(accountID) && ev.listID.equals(followList.id)){
			data.add(new AccountViewModel(ev.account, accountID));
			list.getAdapter().notifyItemInserted(data.size()-1);
		}
	}

	private void onFabClick(){
		Bundle args=new Bundle();
		args.putString("account", accountID);
		Nav.goForResult(getActivity(), AddListMembersFragment.class, args, ADD_MEMBER_RESULT, this);
	}

	private void onItemClick(AccountViewHolder holder){
		if(inSelectionMode){
			String id=holder.getItem().account.id;
			if(selectedAccounts.contains(id)){
				selectedAccounts.remove(id);
				holder.setChecked(false);
			}else{
				selectedAccounts.add(id);
				holder.setChecked(true);
			}
			updateActionModeTitle();
			deleteItem.setEnabled(!selectedAccounts.isEmpty());
			return;
		}
		Bundle args=new Bundle();
		args.putString("account", accountID);
		args.putParcelable("profileAccount", Parcels.wrap(holder.getItem().account));
		Nav.go(getActivity(), ProfileFragment.class, args);
	}

	private boolean onItemLongClick(AccountViewHolder holder){
		if(inSelectionMode)
			return false;
		selectedAccounts.add(holder.getItem().account.id);
		enterSelectionMode();
		return true;
	}

	private void onItemMenuItemSelected(AccountViewHolder holder, MenuItem item){
		int id=item.getItemId();
		if(id==R.id.remove_from_list){
			new M3AlertDialogBuilder(getActivity())
					.setTitle(R.string.confirm_remove_list_member)
					.setPositiveButton(R.string.remove, (dlg, which)->removeAccounts(Set.of(holder.getItem().account.id)))
					.setNegativeButton(R.string.cancel, null)
					.show();
		}
	}

	private void updateItemsForSelectionModeTransition(){
		list.getAdapter().notifyItemRangeChanged(0, data.size());
	}

	private void enterSelectionMode(){
		inSelectionMode=true;
		updateItemsForSelectionModeTransition();
		V.setVisibilityAnimated(fab, View.INVISIBLE);
		actionMode=ActionModeHelper.startActionMode(this, ()->elevationOnScrollListener.getCurrentStatusBarColor(), new ActionMode.Callback(){
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu){
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu){
				mode.getMenuInflater().inflate(R.menu.settings_filter_words_action_mode, menu);
				deleteItem=menu.findItem(R.id.delete);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item){
				new M3AlertDialogBuilder(getActivity())
						.setTitle(R.string.confirm_remove_list_members)
						.setPositiveButton(R.string.remove, (dlg, which)->removeAccounts(new HashSet<>(selectedAccounts)))
						.setNegativeButton(R.string.cancel, null)
						.show();
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode){
				actionMode=null;
				inSelectionMode=false;
				selectedAccounts.clear();
				updateItemsForSelectionModeTransition();
				V.setVisibilityAnimated(fab, View.VISIBLE);
			}
		});
		updateActionModeTitle();
	}

	private void updateActionModeTitle(){
		actionMode.setTitle(getResources().getQuantityString(R.plurals.x_items_selected, selectedAccounts.size(), selectedAccounts.size()));
	}

	private void removeAccounts(Set<String> ids){
		new RemoveAccountsFromList(followList.id, ids)
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(Void result){
						if(inSelectionMode)
							actionMode.finish();
						removeAccountRows(ids);
					}

					@Override
					public void onError(ErrorResponse error){
						error.showToast(getActivity());
					}
				})
				.wrapProgress(getActivity(), R.string.loading, true)
				.exec(accountID);
	}

	private void addAccounts(Collection<Account> accounts){
		new AddAccountsToList(followList.id, accounts.stream().map(a->a.id).collect(Collectors.toSet()))
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(Void result){
						for(Account acc:accounts){
							data.add(new AccountViewModel(acc, accountID));
						}
						list.getAdapter().notifyItemRangeInserted(data.size()-accounts.size(), accounts.size());
					}

					@Override
					public void onError(ErrorResponse error){
						error.showToast(getActivity());
					}
				})
				.wrapProgress(getActivity(), R.string.loading, true)
				.exec(accountID);
	}

	private void removeAccountRows(Set<String> ids){
		for(int i=data.size()-1;i>=0;i--){
			if(ids.contains(((ArrayList<AccountViewModel>)data).get(i).account.id)){
				data.remove(i);
				list.getAdapter().notifyItemRemoved(i);
			}
		}
	}

	@Override
	public Uri getWebUri(Uri.Builder base){
		return null;
	}
}
