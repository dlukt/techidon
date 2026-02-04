package de.icod.techidon.events;

import de.icod.techidon.model.FollowList;

public class ListCreatedEvent{
	public final String accountID;
	public final FollowList list;

	public ListCreatedEvent(String accountID, FollowList list){
		this.accountID=accountID;
		this.list=list;
	}
}
