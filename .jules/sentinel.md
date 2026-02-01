## 2023-10-27 - Debug Log Token Leakage
**Vulnerability:** The application was logging the entire JSON response body in `MastodonAPIController` when running in DEBUG mode. This included sensitive fields like `access_token`, `refresh_token`, and `client_secret` during authentication or API calls, potentially exposing user credentials if logs were shared or accessed by other apps.
**Learning:** Logging full network responses is convenient for debugging but dangerous when responses contain secrets. Even in DEBUG builds, sensitive data should never be written to logs in plain text.
**Prevention:** Implement a redaction mechanism for logging that scrubs known sensitive keys from JSON objects before writing them to the log. Always review logging statements for potential sensitive data exposure.
