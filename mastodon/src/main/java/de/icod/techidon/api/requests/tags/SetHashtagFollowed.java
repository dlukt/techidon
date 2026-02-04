package de.icod.techidon.api.requests.tags;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Hashtag;

public class SetHashtagFollowed extends MastodonAPIRequest<Hashtag>{
    public SetHashtagFollowed(String name, boolean followed){
        super(HttpMethod.POST, "/tags/"+name+"/"+(followed ? "follow" : "unfollow"), Hashtag.class);
        setRequestBody(new Object());
    }
}
