package com.axeldev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe FeedCache
 */
@DisplayName("FeedCache Tests")
class FeedCacheTest {

    private FeedCache cache;
    private RssFeed testFeed;

    @BeforeEach
    void setUp() {
        // Create cache with 1 second TTL for testing
        cache = new FeedCache(1000);

        // Create a test RSS feed
        testFeed = new RssFeed();
        testFeed.setTitle("Test Feed");
        testFeed.setLink("https://example.com");
        testFeed.setDescription("Test Description");
    }

    @Test
    @DisplayName("Should cache and retrieve feed successfully")
    void shouldCacheAndRetrieveFeedSuccessfully() {
        // Given
        String url = "https://example.com/feed.xml";

        // When - First call should execute loader
        RssFeed result1 = cache.get(url, () -> testFeed);

        // When - Second call should use cache
        RssFeed result2 = cache.get(url, () -> {
            fail("Loader should not be called when cache hit");
            return null;
        });

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Test Feed", result1.getTitle());
        assertEquals("Test Feed", result2.getTitle());
        assertSame(result1, result2); // Should be same instance from cache
        assertEquals(1, cache.size());
    }

    @Test
    @DisplayName("Should call loader when cache miss")
    void shouldCallLoaderWhenCacheMiss() {
        // Given
        String url = "https://example.com/new-feed.xml";

        // When
        RssFeed result = cache.get(url, () -> testFeed);

        // Then
        assertNotNull(result);
        assertEquals("Test Feed", result.getTitle());
        assertEquals(1, cache.size());
    }

    @Test
    @DisplayName("Should handle null feed from loader")
    void shouldHandleNullFeedFromLoader() {
        // Given
        String url = "https://example.com/null-feed.xml";

        // When
        RssFeed result = cache.get(url, () -> null);

        // Then
        assertNull(result);
        assertEquals(0, cache.size()); // Null feeds should not be cached
    }

    @Test
    @DisplayName("Should maintain separate cache entries for different URLs")
    void shouldMaintainSeparateCacheEntriesForDifferentUrls() {
        // Given
        String url1 = "https://example.com/feed1.xml";
        String url2 = "https://example.com/feed2.xml";

        RssFeed feed1 = new RssFeed();
        feed1.setTitle("Feed 1");

        RssFeed feed2 = new RssFeed();
        feed2.setTitle("Feed 2");

        // When
        RssFeed result1 = cache.get(url1, () -> feed1);
        RssFeed result2 = cache.get(url2, () -> feed2);

        // Then
        assertEquals("Feed 1", result1.getTitle());
        assertEquals("Feed 2", result2.getTitle());
        assertNotSame(result1, result2);
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("Should overwrite existing cache entry")
    void shouldOverwriteExistingCacheEntry() {
        // Given
        String url = "https://example.com/feed.xml";

        RssFeed originalFeed = new RssFeed();
        originalFeed.setTitle("Original Feed");

        RssFeed updatedFeed = new RssFeed();
        updatedFeed.setTitle("Updated Feed");

        // When - Cache original
        cache.get(url, () -> originalFeed);

        // When - Cache updated (same URL)
        RssFeed result = cache.get(url, () -> updatedFeed);

        // Then
        assertEquals("Original Feed", result.getTitle()); // Should still be original (cache hit)
        assertEquals(1, cache.size());
    }

    @Test
    @DisplayName("Should clear all cache entries")
    void shouldClearAllCacheEntries() {
        // Given - Populate cache with multiple entries
        cache.get("https://example.com/feed1.xml", () -> testFeed);
        cache.get("https://example.com/feed2.xml", () -> testFeed);
        cache.get("https://example.com/feed3.xml", () -> testFeed);

        assertEquals(3, cache.size());

        // When
        cache.clear();

        // Then
        assertEquals(0, cache.size());
    }

    @Test
    @DisplayName("Should remove specific cache entry")
    void shouldRemoveSpecificCacheEntry() {
        // Given
        String url1 = "https://example.com/feed1.xml";
        String url2 = "https://example.com/feed2.xml";

        cache.get(url1, () -> testFeed);
        cache.get(url2, () -> testFeed);

        assertEquals(2, cache.size());

        // When
        cache.remove(url1);

        // Then
        assertEquals(1, cache.size());

        // And remaining entry should still work
        RssFeed remaining = cache.get(url2, () -> {
            fail("Should use cache");
            return null;
        });
        assertNotNull(remaining);
    }

    @Test
    @DisplayName("Should handle removal of non-existent entry gracefully")
    void shouldHandleRemovalOfNonExistentEntryGracefully() {
        // Given - Empty cache

        // When - Try to remove non-existent entry
        cache.remove("https://example.com/non-existent.xml");

        // Then - No exception, cache remains empty
        assertEquals(0, cache.size());
    }

    @Test
    @DisplayName("Should return correct cache size")
    void shouldReturnCorrectCacheSize() {
        // Given - Empty cache
        assertEquals(0, cache.size());

        // When - Add entries
        cache.get("url1", () -> testFeed);
        assertEquals(1, cache.size());

        cache.get("url2", () -> testFeed);
        assertEquals(2, cache.size());

        // When - Remove entry
        cache.remove("url1");
        assertEquals(1, cache.size());

        // When - Clear all
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    @DisplayName("Should use different cache durations for different instances")
    void shouldUseDifferentCacheDurationsForDifferentInstances() {
        // Given
        FeedCache shortCache = new FeedCache(100);  // Very short cache
        FeedCache longCache = new FeedCache(60000); // Long cache

        String url = "https://example.com/feed.xml";

        // When - Both caches get the same feed
        RssFeed result1 = shortCache.get(url, () -> testFeed);
        RssFeed result2 = longCache.get(url, () -> testFeed);

        // Then - Both should have the feed cached
        assertEquals(1, shortCache.size());
        assertEquals(1, longCache.size());
        assertEquals("Test Feed", result1.getTitle());
        assertEquals("Test Feed", result2.getTitle());
    }
}
