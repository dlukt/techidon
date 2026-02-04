package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class DeleteStatus extends MastodonAPIRequest<Status>{
	public DeleteStatus(String id){
		super(HttpMethod.DELETE, "/statuses/"+id, Status.class);
	}

	public static class Scheduled extends MastodonAPIRequest<Object> {
		public Scheduled(String id) {
			super(HttpMethod.DELETE, "/scheduled_statuses/"+id, Object.class);
		}
	}
}
