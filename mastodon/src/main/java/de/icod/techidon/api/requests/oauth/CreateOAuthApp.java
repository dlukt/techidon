package de.icod.techidon.api.requests.oauth;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.Application;

public class CreateOAuthApp extends MastodonAPIRequest<Application>{
	public CreateOAuthApp(){
		super(HttpMethod.POST, "/apps", Application.class);
		setRequestBody(new Request());
	}

	private static class Request{
		public String clientName="Techidon";
		public String redirectUris=AccountSessionManager.REDIRECT_URI;
		public String scopes=AccountSessionManager.SCOPE;
		public String website="https://github.com/deicod/techidon";
	}
}
