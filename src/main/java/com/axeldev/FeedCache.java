package com.axeldev;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Cache pour les flux RSS afin d'éviter les téléchargements répétés
 */
public class FeedCache {
    private final Map<String, CachedFeed> cache = new ConcurrentHashMap<>();
    private final long cacheDurationMs;

    public FeedCache(long cacheDurationMs) {
        this.cacheDurationMs = cacheDurationMs;
    }

    /**
     * Récupère un flux depuis le cache ou le charge si nécessaire
     * @param url URL du flux
     * @param loader Fonction pour charger le flux si pas en cache
     * @return Le flux RSS
     */
    public RssFeed get(String url, Supplier<RssFeed> loader) {
        CachedFeed cached = cache.get(url);
        if (cached != null && !cached.isExpired()) {
            return cached.feed;
        }

        RssFeed feed = loader.get();
        if (feed != null) {
            cache.put(url, new CachedFeed(feed, System.currentTimeMillis(), cacheDurationMs));
        }
        return feed;
    }

    /**
     * Vide complètement le cache
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Supprime un flux spécifique du cache
     */
    public void remove(String url) {
        if (url != null) {
            cache.remove(url);
        }
    }

    /**
     * Retourne le nombre d'entrées en cache
     */
    public int size() {
        return cache.size();
    }

    /**
     * Classe interne pour stocker un flux en cache avec son timestamp
     */
    private static class CachedFeed {
        final RssFeed feed;
        final long timestamp;
        final long cacheDurationMs;

        CachedFeed(RssFeed feed, long timestamp, long cacheDurationMs) {
            this.feed = feed;
            this.timestamp = timestamp;
            this.cacheDurationMs = cacheDurationMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > cacheDurationMs;
        }
    }
}
