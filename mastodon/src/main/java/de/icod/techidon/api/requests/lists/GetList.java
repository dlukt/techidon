package de.icod.techidon.api.requests.lists;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.FollowList;

public class GetList extends MastodonAPIRequest<FollowList> {
	public GetList(String id) {
		super(HttpMethod.GET, "/lists/" + id, FollowList.class);
	}
}
