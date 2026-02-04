package de.icod.techidon.api.requests.announcements;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class AddAnnouncementReaction extends MastodonAPIRequest<Object> {
	public AddAnnouncementReaction(String id, String emoji) {
		super(HttpMethod.PUT, "/announcements/" + id + "/reactions/" + emoji, Object.class);
		setRequestBody(new Object());
	}
}
