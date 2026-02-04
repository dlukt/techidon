package de.icod.techidon.api.requests.trends;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Card;

import java.util.List;

public class GetTrendingLinks extends MastodonAPIRequest<List<Card>>{
	public GetTrendingLinks(){
		super(HttpMethod.GET, "/trends/links", new TypeToken<>(){});
	}
}
