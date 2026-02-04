package de.icod.techidon.api.requests.instance;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.Instance;

public class GetInstance extends MastodonAPIRequest<Instance>{
	public GetInstance(){
		super(HttpMethod.GET, "/instance", Instance.class);
	}

	public static class V2 extends MastodonAPIRequest<Instance.V2>{
		public V2(){
			super(HttpMethod.GET, "/instance", Instance.V2.class);
		}

		@Override
		protected String getPathPrefix() {
			return "/api/v2";
		}
	}
}
