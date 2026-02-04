package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class PleromaAddStatusReaction extends MastodonAPIRequest<Status> {
    public PleromaAddStatusReaction(String id, String emoji) {
        super(HttpMethod.PUT, "/pleroma/statuses/" + id + "/reactions/" + emoji, Status.class);
		setRequestBody(new Object());
    }
}
