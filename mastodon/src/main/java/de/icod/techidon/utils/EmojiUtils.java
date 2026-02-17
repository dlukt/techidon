package de.icod.techidon.utils;

import de.icod.techidon.model.Emoji;
import de.icod.techidon.model.EmojiCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiUtils {

    /**
     * Groups emojis by category, filtering out those not visible in the picker, and sorts categories by title.
     * This method avoids Java Streams to minimize object allocation.
     *
     * @param emojis The list of emojis to group.
     * @return A sorted list of emoji categories.
     */
    public static List<EmojiCategory> groupCustomEmojis(List<Emoji> emojis) {
        if (emojis == null || emojis.isEmpty()) {
            return Collections.emptyList();
        }

        // Group by category, preserving input order within categories
        Map<String, List<Emoji>> groups = new HashMap<>();
        for (Emoji e : emojis) {
            if (e.visibleInPicker) {
                String category = e.category == null ? "" : e.category;
                List<Emoji> list = groups.get(category);
                if (list == null) {
                    list = new ArrayList<>();
                    groups.put(category, list);
                }
                list.add(e);
            }
        }

        // Convert map entries to EmojiCategory list
        List<EmojiCategory> result = new ArrayList<>(groups.size());
        for (Map.Entry<String, List<Emoji>> entry : groups.entrySet()) {
            result.add(new EmojiCategory(entry.getKey(), entry.getValue()));
        }

        // Sort categories by title
        Collections.sort(result, new Comparator<EmojiCategory>() {
            @Override
            public int compare(EmojiCategory a, EmojiCategory b) {
                // Ensure null safety for title, though it shouldn't be null based on grouping key
                String titleA = a.title == null ? "" : a.title;
                String titleB = b.title == null ? "" : b.title;
                return titleA.compareTo(titleB);
            }
        });

        return result;
    }
}
