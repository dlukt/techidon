package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Attachment;

public class UpdateAttachment extends MastodonAPIRequest<Attachment>{
	public UpdateAttachment(String id, String description){
		super(HttpMethod.PUT, "/media/"+id, Attachment.class);
		setRequestBody(new Body(description));
	}

	private static class Body{
		public String description;

		public Body(String description){
			this.description=description;
		}
	}
}
