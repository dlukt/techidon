package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class SetStatusPinned extends MastodonAPIRequest<Status>{
	public SetStatusPinned(String id, boolean pinned){
		super(HttpMethod.POST, "/statuses/"+id+"/"+(pinned ? "pin" : "unpin"), Status.class);
		setRequestBody(new Object());
	}
}
