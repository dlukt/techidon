package de.icod.techidon.api.requests.lists;

import de.icod.techidon.api.MastodonAPIRequest;
import java.util.List;

public class RemoveList extends MastodonAPIRequest<Object> {
    public RemoveList(String listId){
        super(HttpMethod.DELETE, "/lists/"+listId, Object.class);
    }
}
