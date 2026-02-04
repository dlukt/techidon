package de.icod.techidon.api.requests.accounts;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.model.Account;

public class GetFollowRequests extends HeaderPaginationRequest<Account>{
	public GetFollowRequests(String maxID, int limit){
		super(HttpMethod.GET, "/follow_requests", new TypeToken<>(){});
		if(maxID!=null)
			addQueryParameter("max_id", maxID);
		if(limit>0)
			addQueryParameter("limit", ""+limit);
	}
}
