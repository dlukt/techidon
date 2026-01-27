## 2024-05-23 - Stored XSS in Server Description WebView
**Vulnerability:** `SettingsServerAboutFragment` displayed server extended description in a WebView with JavaScript enabled and without sanitization. A malicious or compromised Mastodon instance could inject scripts via the "About" page content.
**Learning:** Even in "federated" apps, data from other instances (servers) should be treated as untrusted user input. WebViews, even when loading data with a `null` base URL (sandboxed to `about:blank`), can still execute scripts that might attempt phishing or exploit WebView vulnerabilities.
**Prevention:** Always disable JavaScript in WebViews unless strictly necessary. Sanitize HTML content using libraries like Jsoup (e.g., `Jsoup.clean(html, Safelist.relaxed())`) before displaying it in a WebView.

## 2024-05-23 - Incomplete Backup Protection for Older Android Versions
**Vulnerability:** The app excluded sensitive `accounts.json` from backups using `dataExtractionRules` (Android 12+) but failed to define `fullBackupContent` (Android 6-11). Since `minSdk` is 23, devices running Android 6-11 would default to backing up all files, including unencrypted auth tokens, to the cloud.
**Learning:** When supporting a wide range of Android versions, newer security features (like `dataExtractionRules`) often do not backport. Always verify security configurations against the `minSdk`.
**Prevention:** Explicitly define `android:fullBackupContent` in `AndroidManifest.xml` alongside `android:dataExtractionRules` if supporting Android versions below 12.
