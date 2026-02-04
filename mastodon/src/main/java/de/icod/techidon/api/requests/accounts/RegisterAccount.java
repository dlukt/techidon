package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Token;

public class RegisterAccount extends MastodonAPIRequest<Token>{
	public RegisterAccount(String username, String email, String password, String locale, String reason, String timezone, String inviteCode){
		super(HttpMethod.POST, "/accounts", Token.class);
		setRequestBody(new Body(username, email, password, locale, reason, timezone, inviteCode));
	}

	private static class Body{
		public String username, email, password, locale, reason, timeZone, inviteCode;
		public boolean agreement=true;

		public Body(String username, String email, String password, String locale, String reason, String timeZone, String inviteCode){
			this.username=username;
			this.email=email;
			this.password=password;
			this.locale=locale;
			this.reason=reason;
			this.timeZone=timeZone;
			this.inviteCode=inviteCode;
		}
	}
}
