## 2025-02-12 - [Expensive Measurement in onBindViewHolder]
**Learning:** `text.measure()` is extremely expensive when called inside `onBindViewHolder` (scrolling path). It forces a layout pass on the TextView. In this codebase, it was being called unconditionally for every text status item to determine if it should be collapsed, even if the result was already known or the feature was disabled.
**Action:** Always check conditions (like user preferences or cached state) *before* performing expensive view measurements in `onBind`.
