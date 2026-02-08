## 2024-05-23 - Accessibility of Dynamic Content
**Learning:** Reusing existing plural string resources (like `sk_users_reacted_with`) is a great way to improve accessibility for dynamic content (emoji reactions) without adding new translation burden.
**Action:** Always check `strings_sk.xml` (inherited from Megalodon) for useful plural strings before creating new ones, especially for features like reactions, polls, or lists.

## 2024-05-27 - Accessibility of Poll Options
**Learning:** Setting `contentDescription` on the parent container (itemView) for complex list items like poll options provides a cohesive and clearer experience than relying on screen readers to navigate children (text + percentage), especially when state changes (results shown/hidden).
**Action:** When implementing interactive list items with multiple text components that form a single logical unit, always set a composite `contentDescription` on the parent view and update it when the state changes.

## 2025-10-27 - Dynamic Accessibility Labels for Toggle Buttons
**Learning:** Toggle buttons (like the emoji keyboard toggle) often retain static accessibility labels (e.g., "Emoji") regardless of state, confusing screen reader users about the action (Open vs Close).
**Action:** Always check toggle buttons for dynamic state updates and ensure `contentDescription` and `tooltipText` are updated programmatically to reflect the *current* action (e.g., "Close emoji keyboard" when open).
