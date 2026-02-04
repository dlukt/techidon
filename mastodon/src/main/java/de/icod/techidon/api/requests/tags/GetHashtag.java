package de.icod.techidon.api.requests.tags;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Hashtag;

public class GetHashtag extends MastodonAPIRequest<Hashtag> {
    public GetHashtag(String name){
        super(HttpMethod.GET, "/tags/"+name, Hashtag.class);
    }
}

