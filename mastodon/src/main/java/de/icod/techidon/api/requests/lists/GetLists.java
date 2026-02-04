package de.icod.techidon.api.requests.lists;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.FollowList;

import java.util.List;

public class GetLists extends MastodonAPIRequest<List<FollowList>>{
    public GetLists() {
        super(HttpMethod.GET, "/lists", new TypeToken<>(){});
    }
    public GetLists(String accountID) {
        super(HttpMethod.GET, "/accounts/"+accountID+"/lists", new TypeToken<>(){});
    }
}
