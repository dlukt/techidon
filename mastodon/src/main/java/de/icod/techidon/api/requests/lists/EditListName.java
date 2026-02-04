package de.icod.techidon.api.requests.lists;

import de.icod.techidon.api.MastodonAPIRequest;
import java.util.List;

public class EditListName extends MastodonAPIRequest<Object> {
    public EditListName(String newListName, String listId){
        super(HttpMethod.PUT, "/lists/"+listId, Object.class);
        Request req = new Request();
        req.title = newListName;
        setRequestBody(req);
    }

    public static class Request{
        public String title;
    }
}
