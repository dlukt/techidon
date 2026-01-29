## 2026-01-27 - Dynamic Content Descriptions for Toggle Buttons

**Learning:** AccessibilityDelegates are powerful for providing dynamic text, but standard `ContentDescription` set directly on views is often more robust for state changes, especially when coupled with `setTooltipText` (API 26+). Updating these properties optimistically in click listeners ensures immediate feedback for screen reader users, matching the visual state change.

**Action:** When implementing toggle buttons (Like/Boost/Bookmark), always ensure the `contentDescription` reflects the *current* state (e.g., "Remove bookmark" vs "Bookmark") and update it immediately upon interaction, not just during data binding.

## 2026-02-05 - Accessible Poll Deletion

**Learning:** Drag-and-drop deletion is a major accessibility barrier. Providing an alternative explicit action (like a delete button) is essential for keyboard and screen reader users.

**Action:** When implementing complex interactions like drag-to-reorder/delete, always pair them with simple, click-based alternatives.

## 2025-05-23 - Ripple Feedback on Colored Backgrounds

**Learning:** Standard list items often have a background color (like `?colorM3SurfaceVariant` for highlighted or special items). Applying `android:background="?selectableItemBackground"` on these removes the color. The correct pattern is to use `android:foreground="?selectableItemBackground"`, which layers the ripple *over* the existing background, preserving the design while adding interaction feedback.

**Action:** Use `android:foreground` for ripple effects on Views that already have a defined background color.

## 2025-05-24 - Status Icons Accessibility

**Learning:** Setting `android:contentDescription` on an ImageView while also setting `android:importantForAccessibility="no"` is an anti-pattern. It suggests the developer recognized the semantic value (by providing a description) but then mistakenly hid it from assistive technologies, likely thinking it was redundant visual decoration. For status icons like "Bot" or "Locked", this information is critical and not always redundant with the text.

**Action:** Always check `android:importantForAccessibility` when you see `android:contentDescription`. If the icon conveys unique status information, remove `importantForAccessibility="no"`.
