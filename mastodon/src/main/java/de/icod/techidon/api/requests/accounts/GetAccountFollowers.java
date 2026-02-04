package de.icod.techidon.api.requests.accounts;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.model.Account;

public class GetAccountFollowers extends HeaderPaginationRequest<Account>{
	public GetAccountFollowers(String id, String maxID, int limit){
		super(HttpMethod.GET, "/accounts/"+id+"/followers", new TypeToken<>(){});
		if(maxID!=null)
			addQueryParameter("max_id", maxID);
		if(limit>0)
			addQueryParameter("limit", limit+"");
	}
}
