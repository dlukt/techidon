package de.icod.techidon.events;

import de.icod.techidon.model.FollowList;

public class ListUpdatedEvent{
	public final String accountID;
	public final FollowList list;

	public ListUpdatedEvent(String accountID, FollowList list){
		this.accountID=accountID;
		this.list=list;
	}
}
