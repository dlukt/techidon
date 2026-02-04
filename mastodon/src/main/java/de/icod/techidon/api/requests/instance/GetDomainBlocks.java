package de.icod.techidon.api.requests.instance;

import com.google.gson.reflect.TypeToken;

import de.icod.techidon.api.MastodonAPIRequest;
import de.icod.techidon.model.DomainBlock;
import de.icod.techidon.model.ExtendedDescription;

import java.util.List;

public class GetDomainBlocks extends MastodonAPIRequest<List<DomainBlock>>{
	public GetDomainBlocks(){
		super(HttpMethod.GET, "/instance/domain_blocks", new TypeToken<>(){});
	}

}
