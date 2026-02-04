package de.icod.techidon.events;

public class ListDeletedEvent{
	public final String accountID;
	public final String listID;

	public ListDeletedEvent(String accountID, String listID){
		this.accountID=accountID;
		this.listID=listID;
	}
}
