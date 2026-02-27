package de.icod.techidon.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Locale;

public class SecurityUtils {

	/**
	 * Checks if a URL has an unsafe scheme that should be blocked to prevent
	 * XSS, local file access, or arbitrary intent launching.
	 *
	 * This method enforces a whitelist of safe schemes. Any scheme not in the whitelist
	 * (including javascript, file, content, intent, etc.) is considered unsafe.
	 *
	 * Blocked schemes: javascript, vbscript, file, content, data, jar, intent.
	 * Allowed schemes: http, https, mailto, tel, xmpp, matrix, magnet, geo.
	 *
	 * @param url The URL to check.
	 * @return True if the URL is considered unsafe (not whitelisted), false otherwise.
	 */
	public static boolean isUnsafeUrl(String url) {
		if (url == null || url.length() == 0) return true;
		try {
			// 🛡️ Sentinel: Use whitelist enforcement instead of blacklist
			// Using java.net.URI for stricter parsing and better testability
			java.net.URI uri = new java.net.URI(url);
			String scheme = uri.getScheme();
			// If scheme is null, it's considered unsafe (fail closed)
			return !isWhitelistedScheme(scheme);
		} catch (Exception e) {
			// Fail closed on parsing errors
			return true;
		}
	}

	/**
	 * Checks if a URL scheme is in the whitelist of safe schemes.
	 *
	 * Allowed schemes: http, https, mailto, tel, xmpp, matrix, magnet, geo.
	 *
	 * @param scheme The scheme to check (e.g., from Uri.getScheme()).
	 * @return True if the scheme is whitelisted, false otherwise.
	 */
	public static boolean isWhitelistedScheme(String scheme) {
		if (scheme == null) return false;
		scheme = scheme.toLowerCase(Locale.US);
		return "http".equals(scheme) ||
				"https".equals(scheme) ||
				"mailto".equals(scheme) ||
				"tel".equals(scheme) ||
				"xmpp".equals(scheme) ||
				"matrix".equals(scheme) ||
				"magnet".equals(scheme) ||
				"geo".equals(scheme);
	}

	/**
	 * Checks if a host (domain) is in the list of blocked domains.
	 *
	 * @param host The host to check.
	 * @param blockedDomains The list of blocked domains.
	 * @return True if the host is blocked, false otherwise.
	 */
	public static boolean isDomainBlocked(String host, Iterable<String> blockedDomains) {
		if (host == null) return true; // Fail closed
		// Normalize: remove trailing dot if present
		if (host.endsWith(".")) {
			host = host.substring(0, host.length() - 1);
		}
		// Convert to lowercase using Locale.ROOT to avoid locale-dependent issues (e.g. Turkish I)
		String lowerHost = host.toLowerCase(Locale.ROOT);
		for (String badDomain : blockedDomains) {
			// badDomains are expected to be lowercase already
			if (lowerHost.equals(badDomain) || lowerHost.endsWith("." + badDomain)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sanitizes a file name to prevent path traversal and ensure a safe basename.
	 *
	 * @param displayName The display name (potentially containing path info).
	 * @return A sanitized basename safe for use, or "file" if the name is invalid.
	 */
	public static String sanitizeFileName(String displayName) {
		if (displayName == null) return null;

		// 1. Decode potential URL-encoded characters to prevent bypasses
		// Basic decoding for common separators
		displayName = displayName.replace("%2e", ".").replace("%2E", ".")
				.replace("%2f", "/").replace("%2F", "/")
				.replace("%5c", "\\").replace("%5C", "\\");

		// 2. Get the last part of the path (handle both / and \)
		int lastSlash = Math.max(displayName.lastIndexOf('/'), displayName.lastIndexOf('\\'));
		if (lastSlash >= 0) {
			displayName = displayName.substring(lastSlash + 1);
		}

		// 3. Trim whitespace
		displayName = displayName.trim();

		// 4. Check for reserved names, traversal attempts, or control characters
		// Also block Windows reserved filenames (CON, PRN, AUX, NUL, COM1-9, LPT1-9)
		if (displayName.isEmpty() ||
				displayName.equals(".") ||
				displayName.equals("..") ||
				displayName.matches("(?i)^(con|prn|aux|nul|com[1-9]|lpt[1-9])(\\..*)?$") ||
				displayName.chars().anyMatch(c -> c < 32 || c == 127)) {
			return "file";
		}

		return displayName;
	}
}
