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

    @Test
    public void testIsDomainBlocked() {
        java.util.List<String> blocked = java.util.Arrays.asList("evil.com", "bad.org", "blocked.net");

        // Exact match
        assertTrue(SecurityUtils.isDomainBlocked("evil.com", blocked));
        assertTrue(SecurityUtils.isDomainBlocked("bad.org", blocked));

        // Subdomain match
        assertTrue(SecurityUtils.isDomainBlocked("sub.evil.com", blocked));
        assertTrue(SecurityUtils.isDomainBlocked("deep.sub.evil.com", blocked));

        // Trailing dot (should be blocked now)
        assertTrue(SecurityUtils.isDomainBlocked("evil.com.", blocked));
        assertTrue(SecurityUtils.isDomainBlocked("sub.evil.com.", blocked));

        // Partial match (suffix but not subdomain) - should allowed
        assertFalse(SecurityUtils.isDomainBlocked("not-evil.com", blocked));
        assertFalse(SecurityUtils.isDomainBlocked("good.org", blocked));
        assertFalse(SecurityUtils.isDomainBlocked("realyblocked.net", blocked));

        // Partial match (prefix) - should allowed
        assertFalse(SecurityUtils.isDomainBlocked("evil.com.example.com", blocked));

        // Case insensitivity
        assertTrue(SecurityUtils.isDomainBlocked("EVIL.COM", blocked));
        assertTrue(SecurityUtils.isDomainBlocked("Evil.Com", blocked));
        assertTrue(SecurityUtils.isDomainBlocked("Evil.Com.", blocked));

        // Null
        assertTrue(SecurityUtils.isDomainBlocked(null, blocked));

        // Empty blocklist
        assertFalse(SecurityUtils.isDomainBlocked("evil.com", java.util.Collections.emptyList()));
    }
}
