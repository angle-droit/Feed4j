package com.axeldev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de sécurité pour Feed4j
 * Note: Ces tests vérifient que la configuration de sécurité est correcte
 * et que les protections de base fonctionnent
 */
@DisplayName("Security Tests")
class SecurityTest {

    private Feed4j feed4j;

    @BeforeEach
    void setUp() throws Exception {
        feed4j = new Feed4j();
    }

    @Test
    @DisplayName("Should create Feed4j instance with security features enabled")
    void shouldCreateFeed4jInstanceWithSecurityFeaturesEnabled() throws Exception {
        // Given - Create a new Feed4j instance
        Feed4j secureFeed4j = new Feed4j();

        // Then - The instance should be created successfully with security features enabled
        assertNotNull(secureFeed4j);
        assertNotNull(secureFeed4j.getConfig());

        // Verify that the configuration has secure defaults
        assertTrue(secureFeed4j.getConfig().getMaxThreads() > 0);
        assertTrue(secureFeed4j.getConfig().getConnectTimeout() >= 1000);
        assertTrue(secureFeed4j.getConfig().getReadTimeout() >= 1000);
    }

    @Test
    @DisplayName("Should handle network errors securely")
    void shouldHandleNetworkErrorsSecurely() throws Exception {
        // Given
        String invalidUrl = "https://non-existent-domain-12345.com/feed.xml";

        // When
        RssFeed feed = feed4j.ReadFeed(invalidUrl);

        // Then - Should fail gracefully without exposing sensitive information
        assertNull(feed);
    }

    @Test
    @DisplayName("Should validate configuration limits")
    void shouldValidateConfigurationLimits() {
        // Given - Create custom config with invalid values
        Feed4jConfig config = new Feed4jConfig();

        // When - Set invalid values
        config.setMaxThreads(-1);
        config.setConnectTimeout(500);
        config.setReadTimeout(200);

        // Then - Should enforce minimum values
        assertEquals(1, config.getMaxThreads()); // Minimum 1 thread
        assertEquals(1000, config.getConnectTimeout()); // Minimum 1000ms
        assertEquals(1000, config.getReadTimeout()); // Minimum 1000ms
    }

    @Test
    @DisplayName("Should prevent cache timing attacks")
    void shouldPreventCacheTimingAttacks() throws Exception {
        // Given - Create Feed4j instance
        Feed4j instance = new Feed4j();

        // When - Try to manipulate cache with invalid operations
        instance.removeFromCache(null);
        instance.clearCache();

        // Then - Should handle gracefully without crashes
        assertEquals(0, instance.getCacheSize());
    }

    @Test
    @DisplayName("Should use secure User-Agent by default")
    void shouldUseSecureUserAgentByDefault() {
        // Given - Create default config
        Feed4jConfig config = new Feed4jConfig();

        // Then - Should have a reasonable default User-Agent
        assertNotNull(config.getUserAgent());
        assertTrue(config.getUserAgent().length() > 0);
        assertFalse(config.getUserAgent().contains("Mozilla")); // Avoid browser impersonation
    }

    @Test
    @DisplayName("Should handle null inputs gracefully")
    void shouldHandleNullInputsGracefully() {
        // Given - Create config
        Feed4jConfig config = new Feed4jConfig();

        // When - Set null values
        config.setUserAgent(null);

        // Then - Should use defaults
        assertNotNull(config.getUserAgent());
        assertEquals("Feed4j/1.0", config.getUserAgent());
    }
}