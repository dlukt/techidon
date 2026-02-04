package de.icod.techidon.api.requests.statuses;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.EmojiReaction;

import java.util.List;

public class PleromaGetStatusReactions extends MastodonAPIRequest<List<EmojiReaction>> {
    public PleromaGetStatusReactions(String id, String emoji) {
        super(HttpMethod.GET, "/pleroma/statuses/" + id + "/reactions/" + (emoji != null ? emoji : ""), new TypeToken<>(){});
    }
}
