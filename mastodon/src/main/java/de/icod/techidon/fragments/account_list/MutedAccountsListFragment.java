package de.icod.techidon.fragments.account_list;

import android.net.Uri;
import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.api.requests.accounts.GetAccountMutes;
import de.icod.techidon.model.Account;
import de.icod.techidon.ui.viewholders.AccountViewHolder;

@SuppressWarnings("deprecation")

public class MutedAccountsListFragment extends AccountRelatedAccountListFragment{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(R.string.sk_muted_accounts);
	}

	@Override
	public HeaderPaginationRequest<Account> onCreateRequest(String maxID, int count){
		return new GetAccountMutes(maxID, count);
	}

	@Override
	protected void onConfigureViewHolder(AccountViewHolder holder){
		super.onConfigureViewHolder(holder);
		holder.setStyle(AccountViewHolder.AccessoryType.NONE, false);
	}

	@Override
	public Uri getWebUri(Uri.Builder base) {
		return super.getWebUri(base).buildUpon()
				.appendPath("/mutes").build();
	}
}
