## 2026-01-27 - Dynamic Content Descriptions for Toggle Buttons

**Learning:** AccessibilityDelegates are powerful for providing dynamic text, but standard `ContentDescription` set directly on views is often more robust for state changes, especially when coupled with `setTooltipText` (API 26+). Updating these properties optimistically in click listeners ensures immediate feedback for screen reader users, matching the visual state change.

**Action:** When implementing toggle buttons (Like/Boost/Bookmark), always ensure the `contentDescription` reflects the *current* state (e.g., "Remove bookmark" vs "Bookmark") and update it immediately upon interaction, not just during data binding.
