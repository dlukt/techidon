package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class PleromaDeleteStatusReaction extends MastodonAPIRequest<Status> {
    public PleromaDeleteStatusReaction(String id, String emoji) {
        super(HttpMethod.DELETE, "/pleroma/statuses/" + id + "/reactions/" + emoji, Status.class);
    }
}
