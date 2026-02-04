package de.icod.techidon.api.requests.lists;

import de.icod.techidon.api.MastodonAPIRequest;
import java.util.List;

public class AddList extends MastodonAPIRequest<Object> {
    public AddList(String listName){
        super(HttpMethod.POST, "/lists", Object.class);
        Request req = new Request();
        req.title = listName;
        setRequestBody(req);
    }

    public static class Request{
        public String title;
    }
}
