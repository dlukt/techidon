package me.grishka.appkit.api;

import me.grishka.appkit.fragments.BaseRecyclerFragment;

public class PaginatedListCallback<I, T extends PaginatedList<I>> extends SimpleCallback<T>{

	private final BaseRecyclerFragment<I> recyclerFragment;

	public PaginatedListCallback(BaseRecyclerFragment<I> fragment){
		super(fragment);
		recyclerFragment=fragment;
	}

	@Override
	public void onSuccess(T result){
		recyclerFragment.onDataLoaded(result);
	}
}
