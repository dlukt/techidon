package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.RequiredField;
import de.icod.techidon.model.BaseModel;

public class CheckInviteLink extends MastodonAPIRequest<CheckInviteLink.Response>{
	public CheckInviteLink(String path){
		super(HttpMethod.GET, path, Response.class);
		addHeader("Accept", "application/json");
	}

	@Override
	protected String getPathPrefix(){
		return "";
	}

	public static class Response extends BaseModel{
		@RequiredField
		public String inviteCode;
	}
}
