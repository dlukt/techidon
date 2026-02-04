package de.icod.techidon.model;

import de.icod.techidon.api.AllFieldsAreRequired;
import org.parceler.Parcel;

@AllFieldsAreRequired
@Parcel
public class FilterStatus extends BaseModel{
	public String id;
	public String statusId;
}
