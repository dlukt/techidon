package de.icod.techidon.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class SchemeSecurityTest {

    @Test
    public void testIsWhitelistedScheme_rejectsDangerousSchemes() {
        // These schemes are not in the blacklist but could launch external apps
        assertFalse("market scheme should be rejected", SecurityUtils.isWhitelistedScheme("market"));
        assertFalse("samsung-wallet scheme should be rejected", SecurityUtils.isWhitelistedScheme("samsung-wallet"));
        assertFalse("custom scheme should be rejected", SecurityUtils.isWhitelistedScheme("custom"));
        assertFalse("app scheme should be rejected", SecurityUtils.isWhitelistedScheme("app"));
        assertFalse("null scheme should be rejected", SecurityUtils.isWhitelistedScheme(null));
        assertFalse("empty scheme should be rejected", SecurityUtils.isWhitelistedScheme(""));
    }

    @Test
    public void testIsWhitelistedScheme_allowsSafeSchemes() {
        assertTrue("http scheme should be allowed", SecurityUtils.isWhitelistedScheme("http"));
        assertTrue("https scheme should be allowed", SecurityUtils.isWhitelistedScheme("https"));
        assertTrue("mailto scheme should be allowed", SecurityUtils.isWhitelistedScheme("mailto"));
        assertTrue("tel scheme should be allowed", SecurityUtils.isWhitelistedScheme("tel"));
        assertTrue("xmpp scheme should be allowed", SecurityUtils.isWhitelistedScheme("xmpp"));
        assertTrue("matrix scheme should be allowed", SecurityUtils.isWhitelistedScheme("matrix"));
        assertTrue("magnet scheme should be allowed", SecurityUtils.isWhitelistedScheme("magnet"));
        assertTrue("geo scheme should be allowed", SecurityUtils.isWhitelistedScheme("geo"));
    }
}
