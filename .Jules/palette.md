## 2026-01-27 - Dynamic Content Descriptions for Toggle Buttons

**Learning:** AccessibilityDelegates are powerful for providing dynamic text, but standard `ContentDescription` set directly on views is often more robust for state changes, especially when coupled with `setTooltipText` (API 26+). Updating these properties optimistically in click listeners ensures immediate feedback for screen reader users, matching the visual state change.

**Action:** When implementing toggle buttons (Like/Boost/Bookmark), always ensure the `contentDescription` reflects the *current* state (e.g., "Remove bookmark" vs "Bookmark") and update it immediately upon interaction, not just during data binding.

## 2026-02-05 - Accessible Poll Deletion

**Learning:** Drag-and-drop deletion is a major accessibility barrier. Providing an alternative explicit action (like a delete button) is essential for keyboard and screen reader users.

**Action:** When implementing complex interactions like drag-to-reorder/delete, always pair them with simple, click-based alternatives.
