package de.icod.techidon.ui.viewcontrollers;

import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.fragments.CreateListFragment;
import de.icod.techidon.fragments.ManageListsFragment;
import de.icod.techidon.model.FollowList;
import de.icod.techidon.ui.utils.HideableSingleViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import me.grishka.appkit.Nav;

@SuppressWarnings("deprecation")

public class HomeTimelineListsMenuController extends DropdownSubmenuController{
	private final List<FollowList> lists;
	private final HomeTimelineMenuController.Callback callback;
	private HideableSingleViewRecyclerAdapter emptyAdapter;

	public HomeTimelineListsMenuController(ToolbarDropdownMenuController dropdownController, HomeTimelineMenuController.Callback callback){
		super(dropdownController);
		this.lists=new ArrayList<>(callback.getLists());
		this.callback=callback;
		items=new ArrayList<>();
		for(FollowList l:lists){
			items.add(new Item<>(l.title, false, false, l, this::onListSelected));
		}
		items.add(new Item<Void>(dropdownController.getActivity().getString(R.string.create_list), false, true, i->{
			dropdownController.dismiss();
			Bundle args=new Bundle();
			args.putString("account", dropdownController.getAccountID());
			Nav.go(dropdownController.getActivity(), CreateListFragment.class, args);
		}));
		items.add(new Item<Void>(dropdownController.getActivity().getString(R.string.manage_lists), false, false, i->{
			dropdownController.dismiss();
			Bundle args=new Bundle();
			args.putString("account", dropdownController.getAccountID());
			Nav.go(dropdownController.getActivity(), ManageListsFragment.class, args);
		}));
	}

	@Override
	protected CharSequence getBackItemTitle(){
		return dropdownController.getActivity().getString(R.string.lists);
	}

	@Override
	protected void createView(){
		super.createView();
		emptyAdapter=createEmptyView(R.drawable.ic_list_alt_24px, R.string.no_lists_title, R.string.no_lists_subtitle);
		if(lists.isEmpty()){
			mergeAdapter.addAdapter(0, emptyAdapter);
		}
	}

	private void onListSelected(Item<FollowList> item){
		callback.onListSelected(item.parentObject);
		dropdownController.dismiss();
	}
}
