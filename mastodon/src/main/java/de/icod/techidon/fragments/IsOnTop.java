package de.icod.techidon.fragments;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("deprecation")

public interface IsOnTop {
    boolean isOnTop();

    default boolean isRecyclerViewOnTop(@Nullable RecyclerView list) {
        if (list == null) return true;
        return !list.canScrollVertically(-1);
    }
}
