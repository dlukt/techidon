package de.icod.techidon.model;

import de.icod.techidon.api.AllFieldsAreRequired;
import org.parceler.Parcel;

@AllFieldsAreRequired
@Parcel
public class History extends BaseModel{
	public long day; // unixtime
	public int uses;
	public int accounts;

	@Override
	public String toString(){
		return "History{"+
				"day="+day+
				", uses="+uses+
				", accounts="+accounts+
				'}';
	}
}
