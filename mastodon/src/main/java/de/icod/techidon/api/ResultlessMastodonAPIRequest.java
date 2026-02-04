package de.icod.techidon.api;

import com.google.gson.reflect.TypeToken;

public abstract class ResultlessMastodonAPIRequest extends MastodonAPIRequest<Void>{
	public ResultlessMastodonAPIRequest(HttpMethod method, String path){
		super(method, path, (Class<Void>)null);
	}
}
