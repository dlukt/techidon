package de.icod.techidon.fragments;

import android.content.Context;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.statuses.GetBookmarkedStatuses;
import de.icod.techidon.events.RemoveAccountPostsEvent;
import de.icod.techidon.model.FilterContext;
import de.icod.techidon.model.HeaderPaginationList;
import de.icod.techidon.model.Status;

import me.grishka.appkit.api.SimpleCallback;

@SuppressWarnings("deprecation")

public class BookmarkedStatusListFragment extends StatusListFragment{
	private static final String STATE_NEXT_MAX_ID="state_next_max_id";

	private String nextMaxID;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			nextMaxID=savedInstanceState.getString(STATE_NEXT_MAX_ID);
			if(!loaded && dataLoading){
				dataLoading=false;
			}
		}
		if(!loaded && !dataLoading){
			loadData();
		}
	}

	@Override
	public void onAttach(Context activity){
		super.onAttach(activity);
		setTitle(R.string.bookmarks);
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(STATE_NEXT_MAX_ID, nextMaxID);
	}

	@Override
	protected void doLoadData(int offset, int count){
		currentRequest=new GetBookmarkedStatuses(offset==0 ? null : nextMaxID, count)
				.setCallback(new SimpleCallback<>(this){
					@Override
					public void onSuccess(HeaderPaginationList<Status> result){
						if(getActivity()==null) return;
						if(result.nextPageUri!=null)
							nextMaxID=result.nextPageUri.getQueryParameter("max_id");
						else
							nextMaxID=null;
						onDataLoaded(result, nextMaxID!=null);
					}
				})
				.exec(accountID);
	}

	@Override
	protected void onRemoveAccountPostsEvent(RemoveAccountPostsEvent ev){
		// no-op
	}

	@Override
	protected FilterContext getFilterContext() {
		return FilterContext.ACCOUNT;
	}

	@Override
	public Uri getWebUri(Uri.Builder base) {
		return base.path("/bookmarks").build();
	}
}
