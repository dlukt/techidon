package de.icod.techidon.api.requests.filters;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.LegacyFilter;

import java.util.List;

public class GetLegacyFilters extends MastodonAPIRequest<List<LegacyFilter>>{
	public GetLegacyFilters(){
		super(HttpMethod.GET, "/filters", new TypeToken<>(){});
	}
}
