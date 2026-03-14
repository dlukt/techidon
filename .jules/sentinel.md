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

## 2024-03-04 - [Cloud Backup Exposing Secrets]
**Vulnerability:** The Android `backup_rules.xml` and `backup_rules_legacy.xml` were failing to adequately exclude `accounts.json` and its temporary write file `accounts.json~` from all backup targets (like device-transfer). This allowed user tokens, passwords, and private push keys to be exposed in cloud backups or device-to-device transfers.
**Learning:** We need to be exhaustive when declaring XML files for data extraction rules. Always include both `cloud-backup` and `device-transfer` tags. Also be mindful of any temporary `.tmp` or `.~` files that applications use while saving sensitive states to disk.
**Prevention:** Make sure developers audit data-extraction-rules regularly for both cloud backups and device transfers, and also verify that any atomic file writes do not accidentally bypass these rules.
## 2025-03-04 - [Cloud Backup Exposing WebPush Keys]
**Vulnerability:** The Android `backup_rules.xml` and `backup_rules_legacy.xml` were failing to exclude `push.xml` (the shared preferences file for push notifications) from all backup targets (like device-transfer and cloud backups). This file contains the private key generated for decrypting WebPush payloads, which could be exposed.
**Learning:** We need to be exhaustive when declaring XML files for data extraction rules. Shared preference files containing cryptographic keys must always be excluded using `<exclude domain="sharedpref" path="filename.xml"/>`.
**Prevention:** Make sure developers audit data-extraction-rules regularly for both cloud backups and device transfers, and also verify that any new shared preference files containing sensitive data are explicitly excluded.
## 2025-10-24 - Domain Blocklist Bypass via Multiple Trailing Dots
**Vulnerability:** The domain blocklist check in `SecurityUtils` only removed a single trailing dot using `if (host.endsWith("."))`. A malicious domain with multiple trailing dots (e.g., `evil.com..`) could bypass the blocklist because the remaining string (`evil.com.`) would not match the normalized blocked domains list, but could still be resolved by some DNS resolvers or HTTP clients.
**Learning:** When sanitizing strings for security checks, ensure that all occurrences of the problematic pattern are removed, not just the first one. FQDNs can legally end in a dot, and multiple dots can sometimes be processed by underlying HTTP clients or DNS resolvers, bypassing simple string-matching blocklists.
**Prevention:** Use a `while` loop or robust normalization techniques to fully strip trailing characters before performing security validation.
