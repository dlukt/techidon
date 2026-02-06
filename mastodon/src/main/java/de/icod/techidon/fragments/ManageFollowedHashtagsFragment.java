package de.icod.techidon.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.tags.GetFollowedTags;
import de.icod.techidon.api.requests.tags.SetTagFollowed;
import de.icod.techidon.fragments.settings.BaseSettingsFragment;
import de.icod.techidon.model.Hashtag;
import de.icod.techidon.model.HeaderPaginationList;
import de.icod.techidon.model.viewmodel.ListItemWithOptionsMenu;
import de.icod.techidon.ui.M3AlertDialogBuilder;
import de.icod.techidon.ui.utils.UiUtils;

import java.util.stream.Collectors;

import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.api.SimpleCallback;

@SuppressWarnings("deprecation")

public class ManageFollowedHashtagsFragment extends BaseSettingsFragment<Hashtag> implements ListItemWithOptionsMenu.OptionsMenuListener<Hashtag>{
	private static final String STATE_MAX_ID="state_max_id";

	private String maxID;

	public ManageFollowedHashtagsFragment(){
		super(100);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(R.string.manage_hashtags);
		if(savedInstanceState!=null){
			maxID=savedInstanceState.getString(STATE_MAX_ID);
			resetDataOnRestore(savedInstanceState);
		}
		if(savedInstanceState!=null || !loaded){
			loadData();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(STATE_MAX_ID, maxID);
	}

	@Override
	protected void doLoadData(int offset, int count){
		currentRequest=new GetFollowedTags(offset>0 ? maxID : null, count)
				.setCallback(new SimpleCallback<>(this){
					@Override
					public void onSuccess(HeaderPaginationList<Hashtag> result){
						maxID=null;
						if(result.nextPageUri!=null)
							maxID=result.nextPageUri.getQueryParameter("max_id");
						onDataLoaded(result.stream().map(t->{
							int posts=t.getWeekPosts();
							return new ListItemWithOptionsMenu<>(t.name, getResources().getQuantityString(R.plurals.x_posts_recently, posts, posts), ManageFollowedHashtagsFragment.this,
									R.drawable.ic_fluent_tag_24_regular, ManageFollowedHashtagsFragment.this::onItemClick, t, false);
						}).collect(Collectors.toList()), maxID!=null);
					}
				})
				.exec(accountID);
	}

	@Override
	public void onConfigureListItemOptionsMenu(ListItemWithOptionsMenu<Hashtag> item, Menu menu){
		menu.clear();
		menu.add(getString(R.string.unfollow_user, "#"+item.parentObject.name));
	}

	@Override
	public void onListItemOptionSelected(ListItemWithOptionsMenu<Hashtag> item, MenuItem menuItem){
		new M3AlertDialogBuilder(getActivity())
				.setTitle(getString(R.string.unfollow_confirmation, "#"+item.parentObject.name))
				.setPositiveButton(R.string.unfollow, (dlg, which)->doUnfollow(item))
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private void onItemClick(ListItemWithOptionsMenu<Hashtag> item){
		UiUtils.openHashtagTimeline(getActivity(), accountID, item.parentObject);
	}

	private void doUnfollow(ListItemWithOptionsMenu<Hashtag> item){
		new SetTagFollowed(item.parentObject.name, false)
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(Hashtag result){
						int index=data.indexOf(item);
						if(index==-1)
							return;
						data.remove(index);
						list.getAdapter().notifyItemRemoved(index);
					}

					@Override
					public void onError(ErrorResponse error){
						error.showToast(getActivity());
					}
				})
				.wrapProgress(getActivity(), R.string.loading, true)
				.exec(accountID);
	}
}
