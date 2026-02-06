package de.icod.techidon.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.icod.techidon.E;
import de.icod.techidon.R;
import de.icod.techidon.api.requests.lists.UpdateList;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.events.ListUpdatedEvent;
import de.icod.techidon.model.FollowList;
import de.icod.techidon.ui.M3AlertDialogBuilder;

import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;

@SuppressWarnings("deprecation")

public class EditListFragment extends BaseEditListFragment{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(R.string.edit_list);
		loadMembers();
		setHasOptionsMenuCompat(true);
	}

	@Override
	public void onCreateAppMenu(Menu menu, MenuInflater inflater){
		menu.add(R.string.delete_list);
	}

	@Override
	public boolean onAppMenuItemSelected(MenuItem item){
		new M3AlertDialogBuilder(getActivity())
				.setTitle(R.string.delete_list)
				.setMessage(getString(R.string.delete_list_confirm, followList.title))
				.setPositiveButton(R.string.delete, (dlg, which)->doDeleteList())
				.setNegativeButton(R.string.cancel, null)
				.show();
		return true;
	}

	@Override
	public void onDestroy(){
		Activity activity=getActivity();
		if(activity!=null && activity.isChangingConfigurations()){
			super.onDestroy();
			return;
		}
		super.onDestroy();
		String newTitle=titleEdit.getText().toString();
		FollowList.RepliesPolicy newRepliesPolicy=getSelectedRepliesPolicy();
		boolean newExclusive=exclusiveItem.checked;
		if(!newTitle.equals(followList.title) || newRepliesPolicy!=followList.repliesPolicy || newExclusive!=followList.exclusive){
			new UpdateList(followList.id, newTitle, newRepliesPolicy, newExclusive)
					.setCallback(new Callback<>(){
						@Override
						public void onSuccess(FollowList result){
							AccountSessionManager.get(accountID).getCacheController().updateList(result);
							E.post(new ListUpdatedEvent(accountID, result));
						}

						@Override
						public void onError(ErrorResponse error){
							// TODO handle errors somehow
						}
					})
					.exec(accountID);
		}
	}
}
