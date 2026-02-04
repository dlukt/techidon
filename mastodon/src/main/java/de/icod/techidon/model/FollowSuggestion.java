package de.icod.techidon.model;

import de.icod.techidon.api.ObjectValidationException;
import de.icod.techidon.api.RequiredField;

public class FollowSuggestion extends BaseModel{
	@RequiredField
	public Account account;
//	public String source;

	@Override
	public void postprocess() throws ObjectValidationException{
		super.postprocess();
		account.postprocess();
	}
}
