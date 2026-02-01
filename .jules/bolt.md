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
