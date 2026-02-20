package de.icod.techidon.model;

import de.icod.techidon.api.RequiredField;
import de.icod.techidon.ui.text.CustomEmojiSpan;
import org.parceler.Parcel;

import java.time.Instant;
import java.util.ArrayList;

import me.grishka.appkit.imageloader.requests.UrlImageLoaderRequest;

/**
 * Represents a profile field as a name-value pair with optional verification.
 */
@Parcel
public class AccountField extends BaseModel{
	/**
	 * The key of a given field's key-value pair.
	 */
	@RequiredField
	public String name;
	/**
	 * The value associated with the name key.
	 */
	@RequiredField
	public String value;
	/**
	 * Timestamp of when the server verified a URL value for a rel="me” link.
	 */
	public Instant verifiedAt;

	public transient CharSequence parsedValue, parsedName;
	public transient CustomEmojiSpan[] valueEmojis, nameEmojis;
	public transient ArrayList<UrlImageLoaderRequest> emojiRequests;

	/**
	 * Cache for the lowercased version of the name field.
	 * This avoids repeated string allocations and toLowerCase() calls in hot paths like PronounHelper.
	 */
	public transient String lowerCaseName;

	@Override
	public String toString(){
		return "AccountField{"+
				"name='"+name+'\''+
				", value='"+value+'\''+
				", verifiedAt="+verifiedAt+
				'}';
	}
}
