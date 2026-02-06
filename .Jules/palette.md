## 2024-05-23 - Accessibility of Dynamic Content
**Learning:** Reusing existing plural string resources (like `sk_users_reacted_with`) is a great way to improve accessibility for dynamic content (emoji reactions) without adding new translation burden.
**Action:** Always check `strings_sk.xml` (inherited from Megalodon) for useful plural strings before creating new ones, especially for features like reactions, polls, or lists.
