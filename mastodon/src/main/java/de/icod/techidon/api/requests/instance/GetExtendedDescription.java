package de.icod.techidon.api.requests.instance;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.ExtendedDescription;
import de.icod.techidon.model.Instance;

public class GetExtendedDescription extends MastodonAPIRequest<ExtendedDescription>{
	public GetExtendedDescription(){
		super(HttpMethod.GET, "/instance/extended_description", ExtendedDescription.class);
	}

}
