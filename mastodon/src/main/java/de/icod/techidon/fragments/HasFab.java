package de.icod.techidon.fragments;

import android.view.View;

@SuppressWarnings("deprecation")

public interface HasFab {
    View getFab();
    void showFab();
    void hideFab();
    boolean isScrolling();
}
