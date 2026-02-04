package de.icod.techidon.events;

public class StatusUnpinnedEvent {
	public final String id;
	public final String accountID;

	public StatusUnpinnedEvent(String id, String accountID){
		this.id=id;
		this.accountID=accountID;
	}
}
