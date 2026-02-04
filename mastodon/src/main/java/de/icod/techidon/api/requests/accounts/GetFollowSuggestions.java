package de.icod.techidon.api.requests.accounts;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.FollowSuggestion;

import java.util.List;

public class GetFollowSuggestions extends MastodonAPIRequest<List<FollowSuggestion>>{
	public GetFollowSuggestions(int limit){
		super(HttpMethod.GET, "/suggestions", new TypeToken<>(){});
		addQueryParameter("limit", limit+"");
	}

	@Override
	protected String getPathPrefix(){
		return "/api/v2";
	}
}
