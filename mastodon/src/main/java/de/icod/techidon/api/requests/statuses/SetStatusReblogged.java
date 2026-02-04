package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;
import de.icod.techidon.model.StatusPrivacy;

public class SetStatusReblogged extends MastodonAPIRequest<Status>{
	public SetStatusReblogged(String id, boolean reblogged, StatusPrivacy visibility){
		super(HttpMethod.POST, "/statuses/"+id+"/"+(reblogged ? "reblog" : "unreblog"), Status.class);
		Request req = new Request();
		req.visibility = visibility;
		setRequestBody(req);
	}

	public static class Request {
		public StatusPrivacy visibility;
	}
}
