package de.icod.techidon.api.requests.instance;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.WeeklyActivity;

import java.util.List;

public class GetWeeklyActivity extends MastodonAPIRequest<List<WeeklyActivity>>{
	public GetWeeklyActivity(){
		super(HttpMethod.GET, "/instance/activity", new TypeToken<>(){});
	}

}
