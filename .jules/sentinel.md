## 2025-10-21 - Domain Blocklist Bypass via Trailing Dot and Locale
**Vulnerability:** The domain blocklist check in `MastodonAPIController` could be bypassed by appending a trailing dot to the domain name (e.g., `evil.com.`) or by using a locale where `toLowerCase()` behaves unexpectedly (e.g., Turkish 'I').
**Learning:** `Uri.getHost()` preserves the trailing dot of a FQDN. String comparisons for security must always normalize input (strip trailing dot) and use locale-independent case conversion (`Locale.ROOT`).
**Prevention:** Always normalize domains before checking against a blocklist. Use `Locale.ROOT` for all security-critical string manipulations.

## 2025-10-24 - UnifiedPush Token Leakage in Debug Logs
**Vulnerability:** The `UnifiedPushNotificationReceiver` logged the `instance` identifier (which is the secret UnifiedPush token) in DEBUG mode, potentially exposing it to other apps with log access or via bug reports.
**Learning:** Identifiers in third-party libraries (like UnifiedPush's `instance`) can be sensitive capability tokens. Always verify what an identifier represents before logging it.
**Prevention:** Explicitly redact sensitive identifiers in all logs, even in DEBUG builds. Add sensitive keys to centralized redaction utilities.

## 2025-02-18 - Path Traversal in PhotoViewer File Saving
**Vulnerability:** Found unsanitized usage of `Uri.getLastPathSegment()` in `PhotoViewer.java` for constructing filenames when saving files to external storage (Android < 10) and internal cache. Maliciously crafted URLs with encoded path traversal sequences (e.g., `..%2F..%2Fpasswd`) could resolve to directory traversal characters after parsing, potentially overwriting files outside the intended directory.
**Learning:** Android's `Uri.getLastPathSegment()` returns the *decoded* segment. If the last segment contains encoded slash or dot sequences, they are decoded, bypassing simple URL structure checks but introducing filesystem traversal risks if used directly as a filename.
**Prevention:** Always sanitize filenames derived from URLs or user input using a whitelist approach or a dedicated sanitization utility (like `SecurityUtils.sanitizeFileName`) before using them in file operations, regardless of the source.
