package de.icod.techidon.utils;

import androidx.fragment.app.Fragment;
import android.app.assist.AssistContent;
import android.net.Uri;

import de.icod.techidon.fragments.HasAccountID;

@SuppressWarnings("deprecation")

public interface ProvidesAssistContent {
    void onProvideAssistContent(AssistContent assistContent);

    default boolean callFragmentToProvideAssistContent(Fragment fragment, AssistContent assistContent) {
        if (fragment instanceof ProvidesAssistContent assistiveFragment) {
            assistiveFragment.onProvideAssistContent(assistContent);
            return true;
        } else {
            return false;
        }
    }

    interface ProvidesWebUri extends ProvidesAssistContent, HasAccountID {
        Uri getWebUri(Uri.Builder base);

        default Uri.Builder getUriBuilder() {
            return getSession().getInstanceUri().buildUpon();
        }

        default void onProvideAssistContent(AssistContent assistContent) {
            assistContent.setWebUri(getWebUri(getUriBuilder()));
        }
    }
}
