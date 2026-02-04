package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.AkkomaTranslation;

public class AkkomaTranslateStatus extends MastodonAPIRequest<AkkomaTranslation>{
	public AkkomaTranslateStatus(String id, String lang){
		super(HttpMethod.GET, "/statuses/"+id+"/translations/"+lang.toLowerCase(), AkkomaTranslation.class);
	}
}
