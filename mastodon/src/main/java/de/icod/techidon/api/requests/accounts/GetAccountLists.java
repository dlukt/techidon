package de.icod.techidon.api.requests.accounts;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.FollowList;

import java.util.List;

public class GetAccountLists extends MastodonAPIRequest<List<FollowList>>{
	public GetAccountLists(String id){
		super(HttpMethod.GET, "/accounts/"+id+"/lists", new TypeToken<>(){});
	}
}
