package de.icod.techidon.ui.text;

import android.text.SpannableStringBuilder;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Collections;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class HtmlParserTest {
    @Test
    public void testJavascriptLinkIgnored() {
        String html = "<a href=\"javascript:alert(1)\">Click me</a>";
        SpannableStringBuilder ssb = HtmlParser.parse(html, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "1");
        LinkSpan[] spans = ssb.getSpans(0, ssb.length(), LinkSpan.class);
        assertEquals("Should not create LinkSpan for javascript: URI", 0, spans.length);
        assertEquals("Should retain text content", "Click me", ssb.toString());
    }

    @Test
    public void testFileLinkIgnored() {
        String html = "<a href=\"file:///etc/passwd\">Click me</a>";
        SpannableStringBuilder ssb = HtmlParser.parse(html, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "1");
        LinkSpan[] spans = ssb.getSpans(0, ssb.length(), LinkSpan.class);
        assertEquals("Should not create LinkSpan for file: URI", 0, spans.length);
    }

    @Test
    public void testValidLinkAllowed() {
        String html = "<a href=\"https://mastodon.social\">Click me</a>";
        SpannableStringBuilder ssb = HtmlParser.parse(html, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "1");
        LinkSpan[] spans = ssb.getSpans(0, ssb.length(), LinkSpan.class);
        assertEquals("Should create LinkSpan for https: URI", 1, spans.length);
        assertEquals("https://mastodon.social", spans[0].getLink());
    }

    @Test
    public void testIntentLinkIgnored() {
        String html = "<a href=\"intent://example.com#Intent;scheme=http;package=com.example;end\">Click me</a>";
        SpannableStringBuilder ssb = HtmlParser.parse(html, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "1");
        LinkSpan[] spans = ssb.getSpans(0, ssb.length(), LinkSpan.class);
        assertEquals("Should not create LinkSpan for intent: URI", 0, spans.length);
    }
}
