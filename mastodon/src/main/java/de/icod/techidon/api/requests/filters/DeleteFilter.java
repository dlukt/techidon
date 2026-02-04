package de.icod.techidon.api.requests.filters;

import de.icod.techidon.api.ResultlessMastodonAPIRequest;

public class DeleteFilter extends ResultlessMastodonAPIRequest{
	public DeleteFilter(String id){
		super(HttpMethod.DELETE, "/filters/"+id);
	}

	@Override
	protected String getPathPrefix(){
		return "/api/v2";
	}
}
