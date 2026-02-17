package de.icod.techidon.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import de.icod.techidon.model.Emoji;
import de.icod.techidon.model.EmojiCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmojiUtilsTest {

    @Test
    public void testGroupCustomEmojis_emptyList() {
        List<EmojiCategory> result = EmojiUtils.groupCustomEmojis(new ArrayList<>());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGroupCustomEmojis_nullList() {
        List<EmojiCategory> result = EmojiUtils.groupCustomEmojis(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGroupCustomEmojis_filtering() {
        Emoji e1 = new Emoji();
        e1.shortcode = "e1";
        e1.visibleInPicker = true;
        e1.category = "cat1";

        Emoji e2 = new Emoji();
        e2.shortcode = "e2";
        e2.visibleInPicker = false;
        e2.category = "cat1";

        List<Emoji> input = Arrays.asList(e1, e2);
        List<EmojiCategory> result = EmojiUtils.groupCustomEmojis(input);

        assertEquals(1, result.size());
        assertEquals("cat1", result.get(0).title);
        assertEquals(1, result.get(0).emojis.size());
        assertEquals("e1", result.get(0).emojis.get(0).shortcode);
    }

    @Test
    public void testGroupCustomEmojis_groupingAndSorting() {
        Emoji e1 = new Emoji();
        e1.shortcode = "e1";
        e1.visibleInPicker = true;
        e1.category = "B_Cat";

        Emoji e2 = new Emoji();
        e2.shortcode = "e2";
        e2.visibleInPicker = true;
        e2.category = "A_Cat";

        Emoji e3 = new Emoji();
        e3.shortcode = "e3";
        e3.visibleInPicker = true;
        e3.category = "B_Cat"; // Same as e1

        List<Emoji> input = Arrays.asList(e1, e2, e3);
        List<EmojiCategory> result = EmojiUtils.groupCustomEmojis(input);

        assertEquals(2, result.size());

        // Sorted by title
        assertEquals("A_Cat", result.get(0).title);
        assertEquals("B_Cat", result.get(1).title);

        // Check content of A_Cat
        assertEquals(1, result.get(0).emojis.size());
        assertEquals("e2", result.get(0).emojis.get(0).shortcode);

        // Check content of B_Cat (order preserved)
        assertEquals(2, result.get(1).emojis.size());
        assertEquals("e1", result.get(1).emojis.get(0).shortcode);
        assertEquals("e3", result.get(1).emojis.get(1).shortcode);
    }

    @Test
    public void testGroupCustomEmojis_nullCategory() {
        Emoji e1 = new Emoji();
        e1.shortcode = "e1";
        e1.visibleInPicker = true;
        e1.category = null;

        List<Emoji> input = Arrays.asList(e1);
        List<EmojiCategory> result = EmojiUtils.groupCustomEmojis(input);

        assertEquals(1, result.size());
        assertEquals("", result.get(0).title);
        assertEquals(1, result.get(0).emojis.size());
    }
}
