package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Relationship;

public class SetAccountMuted extends MastodonAPIRequest<Relationship>{
	public SetAccountMuted(String id, boolean muted, long duration, boolean muteNotifications){
		super(HttpMethod.POST, "/accounts/"+id+"/"+(muted ? "mute" : "unmute"), Relationship.class);
		if(muted)
			setRequestBody(new Request(duration, muteNotifications));
		else{
			setRequestBody(new Object());
		}
	}

	private static class Request{
		public long duration;
		public boolean muteNotifications;
		public Request(long duration, boolean muteNotifications){
			this.duration=duration;
			this.muteNotifications=muteNotifications;
		}
	}
}
