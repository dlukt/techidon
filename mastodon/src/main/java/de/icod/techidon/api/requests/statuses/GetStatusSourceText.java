package de.icod.techidon.api.requests.statuses;

import de.icod.techidon.api.AllFieldsAreRequired;
import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.RequiredField;
import de.icod.techidon.model.BaseModel;
import de.icod.techidon.model.ContentType;

public class GetStatusSourceText extends MastodonAPIRequest<GetStatusSourceText.Response>{
	public GetStatusSourceText(String id){
		super(HttpMethod.GET, "/statuses/"+id+"/source", Response.class);
	}

	public static class Response extends BaseModel{
		@RequiredField
		public String id;
		@RequiredField
		public String text;
		@RequiredField
		public String spoilerText;
		public ContentType contentType;
	}
}
