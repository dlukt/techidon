## 2025-02-12 - [Expensive Measurement in onBindViewHolder]
**Learning:** `text.measure()` is extremely expensive when called inside `onBindViewHolder` (scrolling path). It forces a layout pass on the TextView. In this codebase, it was being called unconditionally for every text status item to determine if it should be collapsed, even if the result was already known or the feature was disabled.
**Action:** Always check conditions (like user preferences or cached state) *before* performing expensive view measurements in `onBind`.

## 2025-05-23 - [Stream Allocation in Hot Paths]
**Learning:** `HtmlParser` uses Java Streams (`stream()`, `collect()`) heavily for parsing mentions, tags, and emojis. This is called for every status item in the RecyclerView (`onBindViewHolder`). On Android, Stream usage allocates many objects, increasing GC pressure and causing jank during scrolling.
**Action:** Replace Streams with simple loops and `HashMap` in critical UI rendering paths to minimize object allocation.

## 2025-05-26 - [BaseStatusListFragment Optimizations]
**Learning:** `BaseStatusListFragment` is the ancestor for all timeline fragments. Optimizations here (like removing Streams in `onAppendItems`) have global impact. The `loadRelationships` method is called frequently during scrolling/pagination, making it a prime target for allocation reduction.
**Action:** Prioritize moving high-frequency logic in `BaseStatusListFragment` from Streams to loops.

## 2025-10-25 - [Stream Allocation in MediaGridStatusDisplayItem]
**Learning:** `MediaGridStatusDisplayItem.onBind` (hot path) was using `Arrays.stream()` to find translated attachments, causing unnecessary object allocations (Stream, Optional, lambda) during scrolling.
**Action:** Replaced stream with a simple loop.

## 2025-10-26 - [Stream Allocation in EmojiReactionsStatusDisplayItem]
**Learning:** `EmojiReactionsStatusDisplayItem` used Java Streams extensively (`filter`, `count`, `findFirst`, `anyMatch`) in `onBind` and `updateReactions`. These are called during scrolling and when reactions update, causing allocation churn.
**Action:** Replaced all stream usages with loops, reducing allocation overhead and improving scrolling smoothness.

## 2025-05-27 - [Stream Allocation in StatusInteractionController]
**Learning:** `StatusInteractionController.setFavorited` used `Stream.filter().findFirst()` to check for existing reactions. This runs on every favorite action (interaction latency).
**Action:** Replaced Stream usage with a single loop to find matches, avoiding object allocation and reducing GC pressure during user interaction.

## 2025-10-27 - [Stream Allocation in UiUtils and HeaderStatusDisplayItem]
**Learning:** `UiUtils.extractPronouns` used complex stream logic (sorted, map, filter, findFirst) which allocated Comparators, Streams, and Optionals. This is called during view binding for account lists. `HeaderStatusDisplayItem` also used streams for visibility toggles and menu updates.
**Action:** Replaced stream logic with simple loops. In `UiUtils`, replaced sort-then-find with a single-pass search for the best match (O(N) vs O(N log N)).

## 2024-05-27 - [Stream Allocation in PreviewlessMediaGridStatusDisplayItem]
**Learning:** `PreviewlessMediaGridStatusDisplayItem.onBind` (hot path) was using `Arrays.stream()` to find translated attachments, causing unnecessary object allocations. This was missed when `MediaGridStatusDisplayItem` was optimized.
**Action:** Replaced stream with a simple loop, following the pattern from `MediaGridStatusDisplayItem`.

## 2025-10-27 - [Stream Allocation in ComposeAutocompleteViewController]
**Learning:** `ComposeAutocompleteViewController.setText` (emoji mode) and `doSearchUsers` were using Java Streams for filtering and mapping during typing. This caused frequent object allocations on the UI thread.
**Action:** Replaced Stream usage with single-pass loops and direct `ArrayList` usage to improve typing responsiveness and reduce GC pressure.
