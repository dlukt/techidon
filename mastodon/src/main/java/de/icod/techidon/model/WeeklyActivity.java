package de.icod.techidon.model;

import de.icod.techidon.api.RequiredField;
import org.parceler.Parcel;

@Parcel
public class WeeklyActivity extends BaseModel {
	@RequiredField
	public String week;
	@RequiredField
	public int statuses;
	@RequiredField
	public int logins;
	@RequiredField
	public int registrations;

	@Override
	public String toString() {
		return "WeeklyActivity{" +
				"week=" + week +
				", statuses=" + statuses +
				", logins=" + logins +
				", registrations=" + registrations +
				'}';
	}
}
