package de.icod.techidon.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

public enum Severity {
	@SerializedName("silence")
	SILENCE,
	@SerializedName("suspend")
	SUSPEND
}