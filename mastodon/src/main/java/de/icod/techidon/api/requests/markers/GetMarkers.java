package de.icod.techidon.api.requests.markers;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.TimelineMarkers;

public class GetMarkers extends MastodonAPIRequest<TimelineMarkers>{
	public GetMarkers(){
		super(HttpMethod.GET, "/markers", TimelineMarkers.class);
		addQueryParameter("timeline[]", "home");
		addQueryParameter("timeline[]", "notifications");
	}
}
