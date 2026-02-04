package de.icod.techidon.api.requests.tags;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Hashtag;

public class GetTag extends MastodonAPIRequest<Hashtag>{
	public GetTag(String tag){
		super(HttpMethod.GET, "/tags/"+tag, Hashtag.class);
	}
}
