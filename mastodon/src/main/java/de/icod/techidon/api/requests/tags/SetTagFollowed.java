package de.icod.techidon.api.requests.tags;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Hashtag;

public class SetTagFollowed extends MastodonAPIRequest<Hashtag>{
	public SetTagFollowed(String tag, boolean followed){
		super(HttpMethod.POST, "/tags/"+tag+(followed ? "/follow" : "/unfollow"), Hashtag.class);
		setRequestBody(new Object());
	}
}
