package com.axeldev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Feed4j
 * Note: Ces tests sont simplifiés pour éviter les dépendances Mockito
 * Pour des tests complets avec mocking HTTP, utiliser WireMock ou Mockito
 */
@DisplayName("Feed4j RSS Parser Tests")
class Feed4jTest {

    private Feed4j feed4j;
    private Feed4jConfig defaultConfig;

    @BeforeEach
    void setUp() throws Exception {
        defaultConfig = new Feed4jConfig();
        feed4j = new Feed4j(defaultConfig);
    }

    @Test
    @DisplayName("Should create Feed4j instance with default config")
    void shouldCreateFeed4jInstanceWithDefaultConfig() throws Exception {
        // When
        Feed4j instance = new Feed4j();

        // Then
        assertNotNull(instance);
        assertNotNull(instance.getConfig());
    }

    @Test
    @DisplayName("Should create Feed4j instance with custom config")
    void shouldCreateFeed4jInstanceWithCustomConfig() throws Exception {
        // Given
        Feed4jConfig customConfig = new Feed4jConfig()
            .setMaxThreads(4)
            .setUserAgent("TestAgent/1.0");

        // When
        Feed4j instance = new Feed4j(customConfig);

        // Then
        assertNotNull(instance);
        assertEquals(4, instance.getConfig().getMaxThreads());
        assertEquals("TestAgent/1.0", instance.getConfig().getUserAgent());
    }

    @Test
    @DisplayName("Should handle network errors gracefully")
    void shouldHandleNetworkErrorsGracefully() throws Exception {
        // Given
        String invalidUrl = "https://non-existent-domain-12345.com/feed.xml";

        // When & Then
        // Should not throw exception but return null
        RssFeed feed = feed4j.ReadFeed(invalidUrl);
        assertNull(feed);
    }

    @Test
    @DisplayName("Should initialize cache correctly")
    void shouldInitializeCacheCorrectly() throws Exception {
        // Given
        Feed4j instance = new Feed4j();

        // Then
        assertEquals(0, instance.getCacheSize());
    }

    @Test
    @DisplayName("Should clear cache successfully")
    void shouldClearCacheSuccessfully() throws Exception {
        // Given
        Feed4j instance = new Feed4j();

        // When
        instance.clearCache();

        // Then
        assertEquals(0, instance.getCacheSize());
    }

    @Test
    @DisplayName("Should remove cache entry successfully")
    void shouldRemoveCacheEntrySuccessfully() throws Exception {
        // Given
        Feed4j instance = new Feed4j();
        String url = "https://example.com/feed.xml";

        // When
        instance.removeFromCache(url);

        // Then
        // Should not throw exception even if entry doesn't exist
        assertEquals(0, instance.getCacheSize());
    }
}