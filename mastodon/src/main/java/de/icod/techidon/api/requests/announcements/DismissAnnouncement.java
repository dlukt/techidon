package de.icod.techidon.api.requests.announcements;

import de.icod.techidon.api.MastodonAPIRequest;

public class DismissAnnouncement extends MastodonAPIRequest<Object>{
	public DismissAnnouncement(String id){
		super(HttpMethod.POST, "/announcements/" + id + "/dismiss", Object.class);
		setRequestBody(new Object());
	}
}
