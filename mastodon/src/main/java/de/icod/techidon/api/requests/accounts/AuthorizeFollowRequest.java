package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Relationship;

public class AuthorizeFollowRequest extends MastodonAPIRequest<Relationship>{
    public AuthorizeFollowRequest(String id){
        super(HttpMethod.POST, "/follow_requests/"+id+"/authorize", Relationship.class);
        setRequestBody(new Object());
    }
}
