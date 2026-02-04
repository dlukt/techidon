package de.icod.techidon.events;

import de.icod.techidon.model.Filter;

public class SettingsFilterCreatedOrUpdatedEvent{
	public final String accountID;
	public final Filter filter;

	public SettingsFilterCreatedOrUpdatedEvent(String accountID, Filter filter){
		this.accountID=accountID;
		this.filter=filter;
	}
}
