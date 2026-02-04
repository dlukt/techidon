package de.icod.techidon.fragments.account_list;

import android.net.Uri;
import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.requests.accounts.GetAccountByHandle;
import de.icod.techidon.api.session.AccountSession;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.Account;
import org.parceler.Parcels;

import java.util.Optional;

@SuppressWarnings("deprecation")

public abstract class AccountRelatedAccountListFragment extends PaginatedAccountListFragment<Account> {
	protected Account account;
	protected String initialSubtitle = "";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		account=Parcels.unwrap(getArguments().getParcelable("targetAccount"));
		if (getArguments().containsKey("remoteAccount")) {
			remoteInfo = Parcels.unwrap(getArguments().getParcelable("remoteAccount"));
		}
		setTitle("@"+account.acct);
	}

	@Override
	public Uri getWebUri(Uri.Builder base) {
		return base.path(isInstanceAkkoma()
				? "/users/" + account.id
				: '@' + account.acct).build();
	}

	@Override
	public String getRemoteDomain() {
		return account.getDomainFromURL();
	}

	@Override
	public Account getCurrentInfo() {
		return doneWithHomeInstance && remoteInfo != null ? remoteInfo : account;
	}

	@Override
	protected MastodonAPIRequest<Account> loadRemoteInfo() {
		return new GetAccountByHandle(account.acct);
	}

	@Override
	protected AccountSession getRemoteSession() {
		return Optional.ofNullable(remoteInfo)
				.map(AccountSessionManager.getInstance()::tryGetAccount)
				.orElse(null);
	}

	@Override
	protected void onRemoteLoadingFailed() {
		super.onRemoteLoadingFailed();
		String prefix = initialSubtitle == null ? "" :
				initialSubtitle + " " + getContext().getString(R.string.sk_separator) + " ";
		String str = prefix +
				getContext().getString(R.string.sk_no_remote_info_hint, getSession().domain);
		setSubtitle(str);
	}
}
