package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class DeleteStatusReaction extends MastodonAPIRequest<Status> {
    public DeleteStatusReaction(String id, String emoji) {
        super(HttpMethod.POST, "/statuses/" + id + "/unreact/" + emoji, Status.class);
		setRequestBody(new Object());
    }
}
