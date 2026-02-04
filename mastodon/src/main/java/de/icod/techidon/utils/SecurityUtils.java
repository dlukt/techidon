package de.icod.techidon.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Locale;

public class SecurityUtils {

	/**
	 * Checks if a URL has an unsafe scheme that should be blocked to prevent
	 * XSS, local file access, or arbitrary intent launching.
	 *
	 * Blocked schemes: javascript, vbscript, file, content, data, jar, intent.
	 *
	 * @param url The URL to check.
	 * @return True if the URL is considered unsafe, false otherwise.
	 */
	public static boolean isUnsafeUrl(String url) {
		if (TextUtils.isEmpty(url)) return true;
		try {
			Uri uri = Uri.parse(url);
			String scheme = uri.getScheme();
			if (scheme == null) return false;
			scheme = scheme.toLowerCase(Locale.US);
			return "javascript".equals(scheme) ||
					"vbscript".equals(scheme) ||
					"file".equals(scheme) ||
					"content".equals(scheme) ||
					"data".equals(scheme) ||
					"jar".equals(scheme) ||
					"intent".equals(scheme) ||
					"blob".equals(scheme);
		} catch (Exception e) {
			return true;
		}
	}
}
