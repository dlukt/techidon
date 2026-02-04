package de.icod.techidon.api.requests.accounts;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Relationship;

public class RejectFollowRequest extends MastodonAPIRequest<Relationship>{
    public RejectFollowRequest(String id){
        super(HttpMethod.POST, "/follow_requests/"+id+"/reject", Relationship.class);
        setRequestBody(new Object());
    }
}
