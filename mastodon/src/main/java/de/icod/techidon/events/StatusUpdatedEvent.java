package de.icod.techidon.events;

import de.icod.techidon.model.Status;

public class StatusUpdatedEvent{
	public Status status;

	public StatusUpdatedEvent(Status status){
		this.status=status;
	}
}
