package de.icod.techidon.api.requests.instance;

import de.icod.techidon.api.MastodonAPIRequest;

import java.time.Instant;

public class GetInstanceExtendedDescription extends MastodonAPIRequest<GetInstanceExtendedDescription.Response>{
	public GetInstanceExtendedDescription(){
		super(HttpMethod.GET, "/instance/extended_description", Response.class);
	}

	public static class Response{
		public Instant updatedAt;
		public String content;
	}
}
