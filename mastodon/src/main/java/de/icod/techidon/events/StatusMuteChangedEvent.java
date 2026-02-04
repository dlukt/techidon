package de.icod.techidon.events;

import de.icod.techidon.model.Status;

public class StatusMuteChangedEvent{
	public String id;
	public boolean muted;
	public Status status;

	public StatusMuteChangedEvent(Status s){
		id=s.id;
		muted=s.muted;
		status=s;
	}
}
