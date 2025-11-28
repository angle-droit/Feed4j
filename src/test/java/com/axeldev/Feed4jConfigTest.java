package com.axeldev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Feed4jConfig
 */
@DisplayName("Feed4jConfig Tests")
class Feed4jConfigTest {

    @Test
    @DisplayName("Should create config with default values")
    void shouldCreateConfigWithDefaultValues() {
        // When
        Feed4jConfig config = new Feed4jConfig();

        // Then
        assertEquals(Runtime.getRuntime().availableProcessors(), config.getMaxThreads());
        assertEquals(10000, config.getConnectTimeout());
        assertEquals(30000, config.getReadTimeout());
        assertEquals("Feed4j/1.0", config.getUserAgent());
        assertFalse(config.isValidateXml());
        assertEquals(300000, config.getCacheDurationMs()); // 5 minutes
    }

    @Test
    @DisplayName("Should support fluent API for configuration")
    void shouldSupportFluentApiForConfiguration() {
        // When
        Feed4jConfig config = new Feed4jConfig()
            .setMaxThreads(4)
            .setConnectTimeout(15000)
            .setReadTimeout(45000)
            .setUserAgent("TestApp/1.0")
            .setValidateXml(true)
            .setCacheDurationMs(600000);

        // Then
        assertEquals(4, config.getMaxThreads());
        assertEquals(15000, config.getConnectTimeout());
        assertEquals(45000, config.getReadTimeout());
        assertEquals("TestApp/1.0", config.getUserAgent());
        assertTrue(config.isValidateXml());
        assertEquals(600000, config.getCacheDurationMs());
    }

    @Test
    @DisplayName("Should validate maxThreads minimum value")
    void shouldValidateMaxThreadsMinimumValue() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When - Setting invalid value
        config.setMaxThreads(0);
        config.setMaxThreads(-5);

        // Then - Should be set to minimum valid value (1)
        assertEquals(1, config.getMaxThreads());
    }

    @Test
    @DisplayName("Should validate timeout minimum values")
    void shouldValidateTimeoutMinimumValues() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When - Setting invalid values
        config.setConnectTimeout(500);  // Below minimum 1000
        config.setReadTimeout(200);     // Below minimum 1000

        // Then - Should be set to minimum valid values
        assertEquals(1000, config.getConnectTimeout());
        assertEquals(1000, config.getReadTimeout());
    }

    @Test
    @DisplayName("Should handle null user agent gracefully")
    void shouldHandleNullUserAgentGracefully() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When
        config.setUserAgent(null);

        // Then - Should use default value
        assertEquals("Feed4j/1.0", config.getUserAgent());
    }

    @Test
    @DisplayName("Should validate cache duration minimum value")
    void shouldValidateCacheDurationMinimumValue() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When - Setting negative cache duration
        config.setCacheDurationMs(-1000);

        // Then - Should be set to 0 (disabled)
        assertEquals(0, config.getCacheDurationMs());
    }

    @Test
    @DisplayName("Should allow zero cache duration to disable caching")
    void shouldAllowZeroCacheDurationToDisableCaching() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When
        config.setCacheDurationMs(0);

        // Then
        assertEquals(0, config.getCacheDurationMs());
    }

    @Test
    @DisplayName("Should maintain state between method calls")
    void shouldMaintainStateBetweenMethodCalls() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When - Multiple chained calls
        config.setMaxThreads(2)
              .setConnectTimeout(20000)
              .setUserAgent("StateTest/1.0");

        // Then - All values should be maintained
        assertEquals(2, config.getMaxThreads());
        assertEquals(20000, config.getConnectTimeout());
        assertEquals("StateTest/1.0", config.getUserAgent());

        // And other values should remain at defaults
        assertEquals(30000, config.getReadTimeout()); // Default
        assertFalse(config.isValidateXml()); // Default
    }

    @Test
    @DisplayName("Should support method chaining with return values")
    void shouldSupportMethodChainingWithReturnValues() {
        // Given
        Feed4jConfig config = new Feed4jConfig();

        // When - Chain multiple setters
        Feed4jConfig result = config
            .setMaxThreads(3)
            .setConnectTimeout(25000)
            .setReadTimeout(50000);

        // Then - Should return the same instance (fluent API)
        assertSame(config, result);
        assertEquals(3, config.getMaxThreads());
        assertEquals(25000, config.getConnectTimeout());
        assertEquals(50000, config.getReadTimeout());
    }

    @Test
    @DisplayName("Should create independent config instances")
    void shouldCreateIndependentConfigInstances() {
        // Given
        Feed4jConfig config1 = new Feed4jConfig()
            .setMaxThreads(2)
            .setUserAgent("Config1");

        Feed4jConfig config2 = new Feed4jConfig()
            .setMaxThreads(4)
            .setUserAgent("Config2");

        // Then - Configurations should be independent
        assertEquals(2, config1.getMaxThreads());
        assertEquals("Config1", config1.getUserAgent());

        assertEquals(4, config2.getMaxThreads());
        assertEquals("Config2", config2.getUserAgent());
    }
}
