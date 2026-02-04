package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Relationship;

public class SetPrivateNote extends MastodonAPIRequest<Relationship>{
    public SetPrivateNote(String id, String comment){
        super(MastodonAPIRequest.HttpMethod.POST, "/accounts/"+id+"/note", Relationship.class);
        Request req = new Request(comment);
        setRequestBody(req);
    }

    private static class Request{
        public String comment;
        public Request(String comment){
            this.comment=comment;
        }
    }
}
