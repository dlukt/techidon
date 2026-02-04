package de.icod.techidon.api;

public interface ProgressListener{
	void onProgress(long transferred, long total);
}
