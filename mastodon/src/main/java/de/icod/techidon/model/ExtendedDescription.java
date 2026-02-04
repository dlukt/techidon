package de.icod.techidon.model;

import de.icod.techidon.api.RequiredField;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ExtendedDescription extends BaseModel{
	@RequiredField
	public String content;
	public String updatedAt;

	@Override
	public String toString() {
		return "ExtendedDescription{" +
				"content='" + content + '\'' +
				", updatedAt='" + updatedAt + '\'' +
				'}';
	}
}
