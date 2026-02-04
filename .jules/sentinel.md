## 2023-10-27 - Debug Log Token Leakage
**Vulnerability:** The application was logging the entire JSON response body in `MastodonAPIController` when running in DEBUG mode. This included sensitive fields like `access_token`, `refresh_token`, and `client_secret` during authentication or API calls, potentially exposing user credentials if logs were shared or accessed by other apps.
**Learning:** Logging full network responses is convenient for debugging but dangerous when responses contain secrets. Even in DEBUG builds, sensitive data should never be written to logs in plain text.
**Prevention:** Implement a redaction mechanism for logging that scrubs known sensitive keys from JSON objects before writing them to the log. Always review logging statements for potential sensitive data exposure.

## 2024-05-23 - WebView Hardening and Crash Prevention
**Vulnerability:** `SettingsServerAboutFragment`'s `WebViewClient` implementation was susceptible to a `NullPointerException` (DoS) when handling URLs with null schemes (e.g. relative URLs), and it implicitly allowed content provider access (`content://`) which is unnecessary and risky. Additionally, `SecurityUtils` did not block `blob:` schemes.
**Learning:** `Uri.getScheme()` can return null, so always check for null before invoking methods on the scheme string. Defense in depth for WebViews should always include `setAllowContentAccess(false)` unless explicitly needed.
**Prevention:** Always enable `setAllowContentAccess(false)` on WebViews. Always null-check schemes from `Uri.parse()`.

## 2026-02-03 - Information Leakage in FileProvider
**Vulnerability:** `TweakedFileProvider` was logging file URIs and selection arguments (potential PII/sensitive queries) to the system log in all builds, including release.
**Learning:** Extending Android components (like `FileProvider`) often encourages overriding methods for debugging, but these overrides must be strictly guarded. Reliance on ProGuard to strip logs is risky.
**Prevention:** Always wrap `Log` calls in `if (BuildConfig.DEBUG)` or use a logging facade. Audit all `FileProvider` subclasses for sensitive logging.

## 2024-05-23 - Sensitive Data Leakage in BroadcastReceivers
**Vulnerability:** `UnifiedPushNotificationReceiver` was logging sensitive endpoint URLs and instance names in release builds via `Log.d`.
**Learning:** BroadcastReceivers handling third-party data (like push endpoints) must be treated as sensitive. `Log.d` is not always automatically stripped and can leak PII or tokens.
**Prevention:** Explicitly guard all sensitive `Log` calls with `if (BuildConfig.DEBUG)`.
