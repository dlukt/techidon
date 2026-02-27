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

    @Test
    public void testIsUnsafeUrl() {
       // Whitelisted schemes should return false (safe)
       assertFalse("http should be safe", SecurityUtils.isUnsafeUrl("http://example.com"));
       assertFalse("https should be safe", SecurityUtils.isUnsafeUrl("https://example.com"));
       assertFalse("mailto should be safe", SecurityUtils.isUnsafeUrl("mailto:user@example.com"));

       // Blacklisted schemes should return true (unsafe)
       assertTrue("javascript should be unsafe", SecurityUtils.isUnsafeUrl("javascript:alert(1)"));
       assertTrue("file should be unsafe", SecurityUtils.isUnsafeUrl("file:///etc/passwd"));
       assertTrue("content should be unsafe", SecurityUtils.isUnsafeUrl("content://provider/path"));

       // Non-whitelisted schemes (custom, etc.) should return true (unsafe) - ENHANCEMENT
       assertTrue("ftp should be unsafe", SecurityUtils.isUnsafeUrl("ftp://example.com"));
       assertTrue("market should be unsafe", SecurityUtils.isUnsafeUrl("market://details?id=com.example"));
       assertTrue("custom scheme should be unsafe", SecurityUtils.isUnsafeUrl("custom:evil"));

       // Malformed URLs should return true (unsafe) due to fail-closed logic
       assertTrue("malformed URL should be unsafe", SecurityUtils.isUnsafeUrl("://"));
       assertTrue("null URL should be unsafe", SecurityUtils.isUnsafeUrl(null));
       assertTrue("empty URL should be unsafe", SecurityUtils.isUnsafeUrl(""));

       // URLs without scheme (relative) are ambiguous but currently treated as unsafe because getScheme() returns null,
       // and isWhitelistedScheme(null) returns false, so !false is true.
       // This is safer default behavior.
       assertTrue("relative URL should be unsafe (no scheme)", SecurityUtils.isUnsafeUrl("/relative/path"));
    }

    @Test
    public void testSanitizeFileName() {
        // Null
        assertNull(SecurityUtils.sanitizeFileName(null));

        // Simple valid names
        assertEquals("simple.jpg", SecurityUtils.sanitizeFileName("simple.jpg"));
        assertEquals("file.txt", SecurityUtils.sanitizeFileName("file.txt"));
        assertEquals("my_photo_2023.png", SecurityUtils.sanitizeFileName("my_photo_2023.png"));

        // Paths (Unix)
        assertEquals("file.png", SecurityUtils.sanitizeFileName("path/to/file.png"));
        assertEquals("file", SecurityUtils.sanitizeFileName("/absolute/path/to/file"));

        // Paths (Windows)
        assertEquals("calc.exe", SecurityUtils.sanitizeFileName("C:\\Windows\\system32\\calc.exe"));
        assertEquals("file.txt", SecurityUtils.sanitizeFileName("foo\\bar\\file.txt"));

        // Mixed separators
        assertEquals("file.txt", SecurityUtils.sanitizeFileName("foo/bar\\file.txt"));

        // Traversal attempts
        assertEquals("passwd", SecurityUtils.sanitizeFileName("../../etc/passwd"));
        assertEquals("boot.ini", SecurityUtils.sanitizeFileName("..\\..\\Windows\\boot.ini"));

        // Edge cases
        assertEquals("file", SecurityUtils.sanitizeFileName(".."));
        assertEquals("file", SecurityUtils.sanitizeFileName("."));
        assertEquals("file", SecurityUtils.sanitizeFileName(""));
        assertEquals("file", SecurityUtils.sanitizeFileName("   ")); // Whitespace only -> empty after trim -> file

        // Whitespace trimming
        assertEquals("spaced.txt", SecurityUtils.sanitizeFileName("  spaced.txt  "));
        assertEquals("file.txt", SecurityUtils.sanitizeFileName("path/to/  file.txt  "));
    }

    @Test
    public void testSanitizeFileName_onPathSegmentLikeInputs() {
        // While Uri.getLastPathSegment() typically returns decoded segments,
        // we should ensure sanitizeFileName handles edge cases defensively.

        // Input resembling raw path traversal attempts
        assertEquals("passwd", SecurityUtils.sanitizeFileName("..%2F..%2Fetc%2Fpasswd"));

        // Input with control characters (which could technically appear if not properly handled upstream)
        assertEquals("file", SecurityUtils.sanitizeFileName("invalid\u0000name"));

        // Windows reserved names as raw segments
        assertEquals("file", SecurityUtils.sanitizeFileName("CON"));
        assertEquals("file", SecurityUtils.sanitizeFileName("nul.txt"));
    }
}
