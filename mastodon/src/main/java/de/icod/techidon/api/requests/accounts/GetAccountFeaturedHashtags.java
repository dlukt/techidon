package de.icod.techidon.api.requests.accounts;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Hashtag;

import java.util.List;

public class GetAccountFeaturedHashtags extends MastodonAPIRequest<List<Hashtag>>{
	public GetAccountFeaturedHashtags(String id){
		super(HttpMethod.GET, "/accounts/"+id+"/featured_tags", new TypeToken<>(){});
	}
}
