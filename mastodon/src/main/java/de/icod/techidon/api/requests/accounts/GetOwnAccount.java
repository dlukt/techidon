package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Account;

public class GetOwnAccount extends MastodonAPIRequest<Account>{
	public GetOwnAccount(){
		super(HttpMethod.GET, "/accounts/verify_credentials", Account.class);
	}
}
