package de.icod.techidon.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class SecurityUtilsTest {

    @Test
    public void testIsWhitelistedScheme() {
        // Safe schemes
        assertTrue(SecurityUtils.isWhitelistedScheme("http"));
        assertTrue(SecurityUtils.isWhitelistedScheme("https"));
        assertTrue(SecurityUtils.isWhitelistedScheme("mailto"));
        assertTrue(SecurityUtils.isWhitelistedScheme("tel"));
        assertTrue(SecurityUtils.isWhitelistedScheme("xmpp"));
        assertTrue(SecurityUtils.isWhitelistedScheme("matrix"));
        assertTrue(SecurityUtils.isWhitelistedScheme("magnet"));
        assertTrue(SecurityUtils.isWhitelistedScheme("geo"));

        // Case insensitivity
        assertTrue(SecurityUtils.isWhitelistedScheme("HTTP"));
        assertTrue(SecurityUtils.isWhitelistedScheme("MailTo"));

        // Unsafe schemes
        assertFalse(SecurityUtils.isWhitelistedScheme("javascript"));
        assertFalse(SecurityUtils.isWhitelistedScheme("file"));
        assertFalse(SecurityUtils.isWhitelistedScheme("content"));
        assertFalse(SecurityUtils.isWhitelistedScheme("intent"));
        assertFalse(SecurityUtils.isWhitelistedScheme("blob"));

        // Custom schemes
        assertFalse(SecurityUtils.isWhitelistedScheme("custom"));
        assertFalse(SecurityUtils.isWhitelistedScheme("app"));

        // Null
        assertFalse(SecurityUtils.isWhitelistedScheme(null));
    }
}
