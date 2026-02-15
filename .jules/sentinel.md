## 2025-10-21 - Domain Blocklist Bypass via Trailing Dot and Locale
**Vulnerability:** The domain blocklist check in `MastodonAPIController` could be bypassed by appending a trailing dot to the domain name (e.g., `evil.com.`) or by using a locale where `toLowerCase()` behaves unexpectedly (e.g., Turkish 'I').
**Learning:** `Uri.getHost()` preserves the trailing dot of a FQDN. String comparisons for security must always normalize input (strip trailing dot) and use locale-independent case conversion (`Locale.ROOT`).
**Prevention:** Always normalize domains before checking against a blocklist. Use `Locale.ROOT` for all security-critical string manipulations.
