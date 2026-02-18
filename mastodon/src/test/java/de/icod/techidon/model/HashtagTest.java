package de.icod.techidon.model;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import static org.junit.Assert.assertEquals;

public class HashtagTest {

    @Test
    public void testGetWeekPosts_CalculatesCorrectSum() {
        Hashtag hashtag = new Hashtag();
        hashtag.history = new ArrayList<>();

        History h1 = new History();
        h1.uses = 10;
        hashtag.history.add(h1);

        History h2 = new History();
        h2.uses = 20;
        hashtag.history.add(h2);

        History h3 = new History();
        h3.uses = 5;
        hashtag.history.add(h3);

        assertEquals(35, hashtag.getWeekPosts());
    }

    @Test
    public void testGetWeekPosts_EmptyHistoryReturnsZero() {
        Hashtag hashtag = new Hashtag();
        hashtag.history = Collections.emptyList();
        assertEquals(0, hashtag.getWeekPosts());
    }

    @Test
    public void testGetWeekPosts_NullHistoryReturnsZero() {
        Hashtag hashtag = new Hashtag();
        hashtag.history = null;
        assertEquals(0, hashtag.getWeekPosts());
    }
}
