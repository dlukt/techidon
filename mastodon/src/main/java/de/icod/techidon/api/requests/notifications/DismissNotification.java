package de.icod.techidon.api.requests.notifications;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.ApiUtils;
import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Notification;

import java.util.EnumSet;
import java.util.List;

public class DismissNotification extends MastodonAPIRequest<Object>{
    public DismissNotification(String id){
        super(HttpMethod.POST, "/notifications/" + (id != null ? id + "/dismiss" : "clear"), Object.class);
        setRequestBody(new Object());
    }
}
