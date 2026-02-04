package de.icod.techidon.api.requests.filters;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Filter;
import de.icod.techidon.model.FilterAction;
import de.icod.techidon.model.FilterContext;
import de.icod.techidon.model.FilterKeyword;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class CreateFilter extends MastodonAPIRequest<Filter>{
	public CreateFilter(String title, EnumSet<FilterContext> context, FilterAction action, int expiresIn, List<FilterKeyword> words){
		super(HttpMethod.POST, "/filters", Filter.class);
		setRequestBody(new FilterRequest(title, context, action, expiresIn==0 ? null : expiresIn, words.stream().map(w->new KeywordAttribute(null, null, w.keyword, w.wholeWord)).collect(Collectors.toList())));
	}

	@Override
	protected String getPathPrefix(){
		return "/api/v2";
	}
}
