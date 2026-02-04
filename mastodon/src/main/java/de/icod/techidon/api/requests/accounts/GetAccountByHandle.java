package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Account;

public class GetAccountByHandle extends MastodonAPIRequest<Account>{
    /**
     * note that this method usually only returns a result if the instance already knows about an
     * account - so it makes sense for looking up local users, search might be preferred otherwise
     */
    public GetAccountByHandle(String acct){
        super(HttpMethod.GET, "/accounts/lookup", Account.class);
        addQueryParameter("acct", acct);
    }
}
