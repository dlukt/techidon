package de.icod.techidon.api.requests.instance;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Emoji;

import java.util.List;

public class GetCustomEmojis extends MastodonAPIRequest<List<Emoji>>{
	public GetCustomEmojis(){
		super(HttpMethod.GET, "/custom_emojis", new TypeToken<>(){});
	}
}
