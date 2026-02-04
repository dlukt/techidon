package de.icod.techidon.api.requests.lists;

import de.icod.techidon.api.ResultlessMastodonAPIRequest;

public class DeleteList extends ResultlessMastodonAPIRequest{
	public DeleteList(String id){
		super(HttpMethod.DELETE, "/lists/"+id);
	}
}
