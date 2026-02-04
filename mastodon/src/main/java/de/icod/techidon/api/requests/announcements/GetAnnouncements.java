package de.icod.techidon.api.requests.announcements;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Announcement;

import java.util.List;

public class GetAnnouncements extends MastodonAPIRequest<List<Announcement>> {
    public GetAnnouncements(boolean withDismissed) {
        super(MastodonAPIRequest.HttpMethod.GET, "/announcements", new TypeToken<>(){});
        addQueryParameter("with_dismissed", withDismissed ? "true" : "false");
    }
}
