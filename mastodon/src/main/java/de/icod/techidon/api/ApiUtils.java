package de.icod.techidon.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ApiUtils{
	private ApiUtils(){
		//no instance
	}

public static <E extends Enum<E>> List<String> enumSetToStrings(EnumSet<E> e, Class<E> cls){
		// Bolt: Replaced stream with loop to reduce allocation overhead when building API requests
		List<String> result = new ArrayList<>(e.size());
		for (E ev : e) {
			try {
				SerializedName annotation = cls.getField(ev.name()).getAnnotation(SerializedName.class);
				result.add(annotation != null ? annotation.value() : ev.name().toLowerCase());
			} catch (NoSuchFieldException x) {
				throw new RuntimeException(x);
			}
		}
		return result;
	}
}
