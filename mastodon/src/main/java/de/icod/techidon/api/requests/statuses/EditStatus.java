package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Status;

public class EditStatus extends MastodonAPIRequest<Status>{
	public EditStatus(CreateStatus.Request req, String id){
		super(HttpMethod.PUT, "/statuses/"+id, Status.class);
		setRequestBody(req);
	}
}
