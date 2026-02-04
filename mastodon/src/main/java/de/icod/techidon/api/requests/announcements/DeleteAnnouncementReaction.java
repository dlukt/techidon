package de.icod.techidon.api.requests.announcements;

import de.icod.techidon.api.MastodonAPIRequest;

public class DeleteAnnouncementReaction extends MastodonAPIRequest<Object> {
	public DeleteAnnouncementReaction(String id, String emoji) {
		super(HttpMethod.DELETE, "/announcements/" + id + "/reactions/" + emoji, Object.class);
	}
}
