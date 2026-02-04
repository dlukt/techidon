package de.icod.techidon.model.catalog;

import de.icod.techidon.api.AllFieldsAreRequired;
import de.icod.techidon.model.BaseModel;

@AllFieldsAreRequired
public class CatalogDefaultInstance extends BaseModel{
	public String domain;
	public float weight;
}
