## 2025-02-12 - [Expensive Measurement in onBindViewHolder]
**Learning:** `text.measure()` is extremely expensive when called inside `onBindViewHolder` (scrolling path). It forces a layout pass on the TextView. In this codebase, it was being called unconditionally for every text status item to determine if it should be collapsed, even if the result was already known or the feature was disabled.
**Action:** Always check conditions (like user preferences or cached state) *before* performing expensive view measurements in `onBind`.

## 2025-05-23 - [Stream Allocation in Hot Paths]
**Learning:** `HtmlParser` uses Java Streams (`stream()`, `collect()`) heavily for parsing mentions, tags, and emojis. This is called for every status item in the RecyclerView (`onBindViewHolder`). On Android, Stream usage allocates many objects, increasing GC pressure and causing jank during scrolling.
**Action:** Replace Streams with simple loops and `HashMap` in critical UI rendering paths to minimize object allocation.
