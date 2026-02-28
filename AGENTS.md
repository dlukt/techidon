# Repository Guidelines

## Project Structure & Module Organization
- `mastodon/` is the main Android app module.
  - `src/main/java/de/icod/techidon/...`: production code
  - `src/main/res/`: UI/resources and locale values
  - `src/debug`, `src/github`, `src/nightly`: build-variant sources
  - `src/test/java` and `src/androidTest/java`: unit and instrumentation tests
- `appkit/` is a shared Android library module (`me.grishka.appkit`).
- `metadata/` contains Play Store listings/changelogs per locale.
- `design/` and `img/` store branding and static assets.
- `.github/workflows/` defines CI checks (Gradle wrapper validation + PR build/tests).

## Build, Test, and Development Commands
- `./gradlew :mastodon:assembleDebug` builds a debug APK.
- `./gradlew :mastodon:testDebugUnitTest` runs local JUnit4 unit tests.
- `./gradlew :mastodon:assembleRelease` builds release artifacts (signing required).
- `./gradlew :mastodon:lintDebug` runs Android lint locally.
- `./gradlew --no-daemon :mastodon:assembleDebug :mastodon:testDebugUnitTest -x lint` mirrors the PR quality gate.

## Coding Style & Naming Conventions
- Target Java 17 (configured via Gradle toolchain).
- Use standard Java naming: `PascalCase` classes, `camelCase` methods/fields, `UPPER_SNAKE_CASE` constants.
- Keep package names consistent with module ownership (`de.icod.techidon`, `me.grishka.appkit`).
- Match the surrounding file style when editing legacy code (indentation varies in older files).
- Keep changes focused; avoid drive-by refactors outside the task.

## Testing Guidelines
- Use JUnit4 for unit tests; place tests in `mastodon/src/test/java` and name files `*Test.java`.
- Add regression tests for security/parsing changes (for examples, see `SecurityUtilsTest` and `RedactSensitiveDataTest`).
- For UI behavior changes, add/update instrumentation tests in `mastodon/src/androidTest/java` when practical.

## Commit & Pull Request Guidelines
- Follow the repo’s commit style: short, imperative subjects; optional prefixes/scopes like `perf:`, `build:`, or `Fix:`.
- Keep PRs focused and include:
  - what changed and why,
  - risk/impact notes,
  - commands used to verify (build/tests),
  - screenshots/video for UI changes.
- Link related issues and ensure CI checks pass before requesting review.

## Security & Configuration Tips
- Never commit secrets or machine-specific values.
- Store signing credentials in environment variables or local, untracked configuration.
- Treat `local.properties` and keystore material as local-only build inputs.
