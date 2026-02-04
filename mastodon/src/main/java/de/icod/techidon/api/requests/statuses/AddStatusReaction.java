package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class AddStatusReaction extends MastodonAPIRequest<Status> {
	public AddStatusReaction(String id, String emoji) {
		super(HttpMethod.POST, "/statuses/" + id + "/react/" + emoji, Status.class);
		setRequestBody(new Object());
	}
}
