## 2024-05-23 - Stored XSS in Server Description WebView
**Vulnerability:** `SettingsServerAboutFragment` displayed server extended description in a WebView with JavaScript enabled and without sanitization. A malicious or compromised Mastodon instance could inject scripts via the "About" page content.
**Learning:** Even in "federated" apps, data from other instances (servers) should be treated as untrusted user input. WebViews, even when loading data with a `null` base URL (sandboxed to `about:blank`), can still execute scripts that might attempt phishing or exploit WebView vulnerabilities.
**Prevention:** Always disable JavaScript in WebViews unless strictly necessary. Sanitize HTML content using libraries like Jsoup (e.g., `Jsoup.clean(html, Safelist.relaxed())`) before displaying it in a WebView.

## 2024-05-23 - Incomplete Backup Protection for Older Android Versions
**Vulnerability:** The app excluded sensitive `accounts.json` from backups using `dataExtractionRules` (Android 12+) but failed to define `fullBackupContent` (Android 6-11). Since `minSdk` is 23, devices running Android 6-11 would default to backing up all files, including unencrypted auth tokens, to the cloud.
**Learning:** When supporting a wide range of Android versions, newer security features (like `dataExtractionRules`) often do not backport. Always verify security configurations against the `minSdk`.
**Prevention:** Explicitly define `android:fullBackupContent` in `AndroidManifest.xml` alongside `android:dataExtractionRules` if supporting Android versions below 12.

## 2024-05-23 - Unsafe URI Scheme Handling in HtmlParser
**Vulnerability:** `HtmlParser` created clickable `LinkSpan`s for any `href` attribute in `<a>` tags, including `javascript:`, `file:`, and `content:` schemes. Opening these links could lead to XSS (via `javascript:`) or local file exposure (via `file:`/`content:`).
**Learning:** Parsing HTML for display often involves creating interactive elements. Validating the URL scheme at the point of parsing is a critical defense-in-depth measure, ensuring that even if the UI layer (e.g. `UiUtils`) is permissive, malicious links are never rendered as clickable.
**Prevention:** Validate URI schemes against an allowlist (or blocklist of known bad schemes) before creating `URLSpan` or custom link spans. Use `Uri.parse()` and check `getScheme()`.

## 2026-01-30 - Intent Redirection via HtmlParser
**Vulnerability:** `HtmlParser` prohibited common unsafe schemes like `javascript` and `file` but allowed the `intent` scheme. This could allow a malicious post to trigger arbitrary Android Intents (Intent Redirection) when a user clicks a link.
**Learning:** Blocklists often miss platform-specific schemes like `intent:`. Whitelisting known safe schemes is generally safer.
**Prevention:** Explicitly filter `intent:` schemes in URL handling logic.
