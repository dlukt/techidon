package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class SetStatusFavorited extends MastodonAPIRequest<Status>{
	public SetStatusFavorited(String id, boolean favorited){
		super(HttpMethod.POST, "/statuses/"+id+"/"+(favorited ? "favourite" : "unfavourite"), Status.class);
		setRequestBody(new Object());
	}
}
