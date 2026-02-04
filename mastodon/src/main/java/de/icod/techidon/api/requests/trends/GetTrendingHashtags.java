package de.icod.techidon.api.requests.trends;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Hashtag;

import java.util.List;

public class GetTrendingHashtags extends MastodonAPIRequest<List<Hashtag>>{
	public GetTrendingHashtags(int limit){
		super(HttpMethod.GET, "/trends", new TypeToken<>(){});
		addQueryParameter("limit", limit+"");
	}
}
