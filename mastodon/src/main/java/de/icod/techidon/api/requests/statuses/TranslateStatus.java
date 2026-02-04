package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Translation;

import java.util.Map;

public class TranslateStatus extends MastodonAPIRequest<Translation>{
	public TranslateStatus(String id, String lang){
		super(HttpMethod.POST, "/statuses/"+id+"/translate", Translation.class);
		setRequestBody(Map.of("lang", lang));
	}
}
