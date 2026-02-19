## 2025-10-21 - Domain Blocklist Bypass via Trailing Dot and Locale
**Vulnerability:** The domain blocklist check in `MastodonAPIController` could be bypassed by appending a trailing dot to the domain name (e.g., `evil.com.`) or by using a locale where `toLowerCase()` behaves unexpectedly (e.g., Turkish 'I').
**Learning:** `Uri.getHost()` preserves the trailing dot of a FQDN. String comparisons for security must always normalize input (strip trailing dot) and use locale-independent case conversion (`Locale.ROOT`).
**Prevention:** Always normalize domains before checking against a blocklist. Use `Locale.ROOT` for all security-critical string manipulations.

## 2025-10-24 - UnifiedPush Token Leakage in Debug Logs
**Vulnerability:** The `UnifiedPushNotificationReceiver` logged the `instance` identifier (which is the secret UnifiedPush token) in DEBUG mode, potentially exposing it to other apps with log access or via bug reports.
**Learning:** Identifiers in third-party libraries (like UnifiedPush's `instance`) can be sensitive capability tokens. Always verify what an identifier represents before logging it.
**Prevention:** Explicitly redact sensitive identifiers in all logs, even in DEBUG builds. Add sensitive keys to centralized redaction utilities.
