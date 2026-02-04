package de.icod.techidon.fragments.account_list;

import android.net.Uri;
import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.api.requests.accounts.GetAccountFollowers;
import de.icod.techidon.model.Account;

@SuppressWarnings("deprecation")

public class FollowerListFragment extends AccountRelatedAccountListFragment{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setSubtitle(initialSubtitle = getResources().getQuantityString(R.plurals.x_followers, (int)(account.followersCount%1000), account.followersCount));
	}

	@Override
	public HeaderPaginationRequest<Account> onCreateRequest(String maxID, int count){
		return new GetAccountFollowers(getCurrentInfo().id, maxID, count);
	}

	@Override
	public Uri getWebUri(Uri.Builder base) {
		return super.getWebUri(base).buildUpon()
				.appendPath(isInstanceAkkoma() ? "#followers" : "/followers").build();
	}
}
