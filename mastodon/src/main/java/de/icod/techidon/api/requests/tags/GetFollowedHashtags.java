package de.icod.techidon.api.requests.tags;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.api.requests.HeaderPaginationRequest;
import de.icod.techidon.model.Hashtag;

import java.util.List;

public class GetFollowedHashtags extends HeaderPaginationRequest<Hashtag> {
    public GetFollowedHashtags() {
        this(null, null, -1, null);
    }

    public GetFollowedHashtags(String maxID, String minID, int limit, String sinceID){
        super(HttpMethod.GET, "/followed_tags", new TypeToken<>(){});
        if(maxID!=null)
            addQueryParameter("max_id", maxID);
        if(minID!=null)
            addQueryParameter("min_id", minID);
        if(sinceID!=null)
            addQueryParameter("since_id", sinceID);
        if(limit>0)
            addQueryParameter("limit", ""+limit);
    }
}

