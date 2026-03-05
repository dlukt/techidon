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
## 2025-03-04 - [Cloud Backup Exposing Push Tokens]
**Vulnerability:** The Android `backup_rules.xml` and `backup_rules_legacy.xml` were failing to exclude `push.xml` (the shared preferences file for push notifications) from all backup targets (like device-transfer and cloud backups). This file currently stores the FCM `deviceToken` and its `version`, which are sensitive identifiers and should not be included in backups, but it does **not** store the WebPush private/public/auth keys (those are persisted in `accounts.json` via `AccountSessionManager.writeAccountsFile()`).
**Learning:** We need to be exhaustive and precise when declaring XML files for data extraction rules. Shared preference files that hold authentication tokens, device identifiers, or cryptographic keys must always be excluded using `<exclude domain="sharedpref" path="filename.xml"/>`, and we must verify where keys are actually persisted.
**Prevention:** Make sure developers audit data-extraction-rules regularly for both cloud backups and device transfers, explicitly excluding any new shared preference files that contain tokens, identifiers, or keys, and cross-checking against where WebPush keys and other secrets are really stored.
