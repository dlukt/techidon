package de.icod.techidon.api.requests.notifications;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Notification;

public class GetNotificationByID extends MastodonAPIRequest<Notification>{
	public GetNotificationByID(String id){
		super(HttpMethod.GET, "/notifications/"+id, Notification.class);
	}
}
