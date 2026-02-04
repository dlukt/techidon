package de.icod.techidon.fragments.account_list;

import android.net.Uri;
import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.api.requests.accounts.GetAccountBlocks;
import de.icod.techidon.model.Account;
import de.icod.techidon.ui.viewholders.AccountViewHolder;

@SuppressWarnings("deprecation")

public class BlocksListFragment extends AccountRelatedAccountListFragment{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(R.string.mo_blocked_accounts);
	}

	@Override
	public HeaderPaginationRequest<Account> onCreateRequest(String maxID, int count){
		return new GetAccountBlocks(maxID, count);
	}

	@Override
	protected void onConfigureViewHolder(AccountViewHolder holder){
		super.onConfigureViewHolder(holder);
		holder.setStyle(AccountViewHolder.AccessoryType.NONE, false);
	}

	@Override
	public Uri getWebUri(Uri.Builder base) {
		return super.getWebUri(base).buildUpon()
				.appendPath("/blocks").build();
	}
}
