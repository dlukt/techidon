package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class SetStatusMuted extends MastodonAPIRequest<Status>{
	public SetStatusMuted(String id, boolean muted){
		super(HttpMethod.POST, "/statuses/"+id+"/"+(muted ? "mute" : "unmute"), Status.class);
		setRequestBody(new Object());
	}
}
