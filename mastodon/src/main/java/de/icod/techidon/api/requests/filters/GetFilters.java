package de.icod.techidon.api.requests.filters;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Filter;

import java.util.List;

public class GetFilters extends MastodonAPIRequest<List<Filter>>{
	public GetFilters(){
		super(HttpMethod.GET, "/filters", new TypeToken<>(){});
	}

	@Override
	protected String getPathPrefix(){
		return "/api/v2";
	}
}
