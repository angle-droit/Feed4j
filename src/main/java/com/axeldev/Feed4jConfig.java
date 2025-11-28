package com.axeldev;

/**
 * Configuration pour Feed4j
 */
public class Feed4jConfig {
    private int maxThreads = Runtime.getRuntime().availableProcessors();
    private int connectTimeout = 10000; // 10 secondes
    private int readTimeout = 30000; // 30 secondes
    private String userAgent = "Feed4j/1.0";
    private boolean validateXml = false;
    private long cacheDurationMs = 300000; // 5 minutes par défaut

    public Feed4jConfig() {}

    // Getters et setters
    public int getMaxThreads() {
        return maxThreads;
    }

    public Feed4jConfig setMaxThreads(int maxThreads) {
        this.maxThreads = Math.max(1, maxThreads);
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public Feed4jConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = Math.max(1000, connectTimeout);
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public Feed4jConfig setReadTimeout(int readTimeout) {
        this.readTimeout = Math.max(1000, readTimeout);
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Feed4jConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent != null ? userAgent : "Feed4j/1.0";
        return this;
    }

    public boolean isValidateXml() {
        return validateXml;
    }

    public Feed4jConfig setValidateXml(boolean validateXml) {
        this.validateXml = validateXml;
        return this;
    }

    public long getCacheDurationMs() {
        return cacheDurationMs;
    }

    public Feed4jConfig setCacheDurationMs(long cacheDurationMs) {
        this.cacheDurationMs = Math.max(0, cacheDurationMs);
        return this;
    }
}
