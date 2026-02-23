## 2024-05-23 - Accessibility of Dynamic Content
**Learning:** Reusing existing plural string resources (like `sk_users_reacted_with`) is a great way to improve accessibility for dynamic content (emoji reactions) without adding new translation burden.
**Action:** Always check `strings_sk.xml` (inherited from Megalodon) for useful plural strings before creating new ones, especially for features like reactions, polls, or lists.

## 2024-05-27 - Accessibility of Poll Options
**Learning:** Setting `contentDescription` on the parent container (itemView) for complex list items like poll options provides a cohesive and clearer experience than relying on screen readers to navigate children (text + percentage), especially when state changes (results shown/hidden).
**Action:** When implementing interactive list items with multiple text components that form a single logical unit, always set a composite `contentDescription` on the parent view and update it when the state changes.

## 2025-10-27 - Dynamic Accessibility Labels for Toggle Buttons
**Learning:** Toggle buttons (like the emoji keyboard toggle) often retain static accessibility labels (e.g., "Emoji") regardless of state, confusing screen reader users about the action (Open vs Close).
**Action:** Always check toggle buttons for dynamic state updates and ensure `contentDescription` and `tooltipText` are updated programmatically to reflect the *current* action (e.g., "Close emoji keyboard" when open).

## 2025-10-27 - Hidden Alt Text Overlays
**Learning:** Overlays designed to display accessibility information (like Alt Text) must never be hidden from accessibility services themselves (e.g., via `importantForAccessibility="noHideDescendants"`). This creates a paradox where the feature meant to help is inaccessible.
**Action:** When auditing accessibility features, check that the containers themselves are accessible and that close buttons have explicit labels.
## 2024-10-26 - Non-visual Feedback for Character Limits
**Learning:** Visual-only cues (like red borders) for character limits exclude screen reader users. Accessibility announcements must be triggered on state transitions (e.g., crossing the limit) to avoid spamming the user on every keystroke.
**Action:** Use `announceForAccessibility` in a `TextWatcher` that tracks state changes (valid <-> invalid) to provide timely context without noise.

## 2025-10-21 - Decorative Image Cleanup
**Learning:** Many list item layouts (`item_discover_account`, `display_item_header`, `display_item_poll_option`) contain decorative `ImageView`s (avatars next to names, unread dots, state icons) that lack `importantForAccessibility="no"`, creating redundant focus targets and noise for screen readers.
**Action:** Audit list item layouts for decorative images and explicitly mark them as `importantForAccessibility="no"` when their information is conveyed by adjacent text or parent container state.

## 2025-10-27 - Semantic Accessibility for Custom Poll Options
**Learning:** Custom interactive list items (like poll options built with `FrameLayout`) often lack semantic roles (CheckBox/RadioButton) and state (Checked/Selected), making them confusing for screen reader users. Using `AccessibilityDelegate` to set the `className` effectively communicates these roles without needing custom strings or changing the UI.
**Action:** Always implement an `AccessibilityDelegate` for custom list items to expose the correct role (e.g., `android.widget.CheckBox`) and state (`isChecked`) to accessibility services.

## 2024-05-28 - Discoverability of Long Press Actions
**Learning:** Hidden long-press features (like "Copy link") on common buttons (Share) are completely invisible to screen reader users unless explicitly exposed as custom accessibility actions.
**Action:** Use `ViewCompat.replaceAccessibilityAction` to add descriptive labels (e.g., "Copy link to post") for `ACTION_LONG_CLICK` on buttons that have long-press listeners.

## 2025-05-23 - Accessibility of Dynamic Content in ViewHolders
**Learning:** `RecyclerView.ViewHolder`s that dynamically generate content (like `EmojiViewHolder`) often miss standard accessibility attributes (contentDescription, tooltipText) that are present in static XML definitions or specialized view classes.
**Action:** When implementing or modifying `onBind` in `RecyclerView.Adapter`, always check if the bound item (like an Emoji) provides a meaningful text representation for `contentDescription` and `tooltipText`.
