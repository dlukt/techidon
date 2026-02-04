package de.icod.techidon.api.requests.catalog;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.catalog.CatalogCategory;
import de.icod.techidon.model.catalog.CatalogInstance;

import java.util.List;

public class GetCatalogCategories extends MastodonAPIRequest<List<CatalogCategory>>{
	private String lang;

	public GetCatalogCategories(String lang){
		super(HttpMethod.GET, null, new TypeToken<>(){});
		this.lang=lang;
	}

	@Override
	public Uri getURL(){
		Uri.Builder builder=new Uri.Builder()
				.scheme("https")
				.authority("api.joinmastodon.org")
				.path("/categories");
		if(!TextUtils.isEmpty(lang))
			builder.appendQueryParameter("language", lang);
		return builder.build();
	}
}
