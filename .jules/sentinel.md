## 2023-10-27 - Debug Log Token Leakage
**Vulnerability:** The application was logging the entire JSON response body in `MastodonAPIController` when running in DEBUG mode. This included sensitive fields like `access_token`, `refresh_token`, and `client_secret` during authentication or API calls, potentially exposing user credentials if logs were shared or accessed by other apps.
**Learning:** Logging full network responses is convenient for debugging but dangerous when responses contain secrets. Even in DEBUG builds, sensitive data should never be written to logs in plain text.
**Prevention:** Implement a redaction mechanism for logging that scrubs known sensitive keys from JSON objects before writing them to the log. Always review logging statements for potential sensitive data exposure.

## 2024-05-23 - WebView Hardening and Crash Prevention
**Vulnerability:** `SettingsServerAboutFragment`'s `WebViewClient` implementation was susceptible to a `NullPointerException` (DoS) when handling URLs with null schemes (e.g. relative URLs), and it implicitly allowed content provider access (`content://`) which is unnecessary and risky. Additionally, `SecurityUtils` did not block `blob:` schemes.
**Learning:** `Uri.getScheme()` can return null, so always check for null before invoking methods on the scheme string. Defense in depth for WebViews should always include `setAllowContentAccess(false)` unless explicitly needed.
**Prevention:** Always enable `setAllowContentAccess(false)` on WebViews. Always null-check schemes from `Uri.parse()`.
