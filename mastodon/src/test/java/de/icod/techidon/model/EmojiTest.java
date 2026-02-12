package de.icod.techidon.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmojiTest {

    @Test
    public void testGetLowerShortcodeCachesResult() {
        Emoji emoji = new Emoji();
        emoji.shortcode = "MixedCase";

        // First call should compute lowercase
        String lower1 = emoji.getLowerShortcode();
        assertEquals("mixedcase", lower1);

        // Second call should return the same string object (cached)
        String lower2 = emoji.getLowerShortcode();
        assertEquals("mixedcase", lower2);
        assertSame("Should return the same string instance", lower1, lower2);
    }

    @Test
    public void testGetLowerShortcodeHandlesNull() {
        Emoji emoji = new Emoji();
        emoji.shortcode = null;
        assertNull(emoji.getLowerShortcode());
    }
}
