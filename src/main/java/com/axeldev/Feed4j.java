package com.axeldev;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Feed4j {

    // Support pour différents formats de date RSS courants
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
        DateTimeFormatter.RFC_1123_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    );

    private final Feed4jConfig config;
    private final FeedCache cache;
    private final DocumentBuilderFactory factory;

    public Feed4j() {
        this(new Feed4jConfig());
    }

    public Feed4j(Feed4jConfig config) {
        this.config = config;
        this.cache = new FeedCache(config.getCacheDurationMs());
        this.factory = DocumentBuilderFactory.newInstance();
        this.factory.setValidating(config.isValidateXml());
        if (config.isValidateXml()) {
            try {
                this.factory.setFeature("http://xml.org/sax/features/validation", true);
                this.factory.setFeature("http://apache.org/xml/features/validation/schema", false);
            } catch (Exception e) {
                // Les features peuvent ne pas être supportées selon l'implémentation
            }
        }
    }

    /**
     * Parse une date depuis une chaîne en essayant plusieurs formats courants
     */
    private static LocalDateTime parsePubDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Essayer le prochain format
            }
        }
        // Si aucun format ne fonctionne, retourner null ou une valeur par défaut
        System.err.println("Format de date non supporté: " + dateStr);
        return null;
    }

    /**
     * Parse les items RSS en parallèle pour améliorer les performances
     */
    private List<RssItem> parseItemsInParallel(NodeList itemNodes) {
        int numThreads = Math.min(itemNodes.getLength(), config.getMaxThreads());
        if (numThreads <= 1) {
            // Pour un petit nombre d'items, utiliser le parsing séquentiel pour éviter l'overhead
            return parseItemsSequentially(itemNodes);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<RssItem> rssItems = new ArrayList<>();

        try {
            List<Future<RssItem>> futures = new ArrayList<>();

            // Soumettre toutes les tâches de parsing
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element itemElement = (Element) itemNodes.item(i);
                Callable<RssItem> task = new ItemParserTask(itemElement);
                futures.add(executor.submit(task));
            }

            // Collecter les résultats en gérant les erreurs individuelles
            for (Future<RssItem> future : futures) {
                try {
                    RssItem item = future.get();
                    if (item != null) {
                        rssItems.add(item);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Logger l'erreur mais continuer avec les autres items
                    System.err.println("Erreur lors du parsing d'un item RSS: " + e.getMessage());
                    // On pourrait utiliser un logger approprié ici
                }
            }

        } finally {
            executor.shutdown();
        }

        return rssItems;
    }

    /**
     * Parsing séquentiel comme fallback pour les petits nombres d'items
     */
    private List<RssItem> parseItemsSequentially(NodeList itemNodes) {
        List<RssItem> rssItems = new ArrayList<>();
        for (int i = 0; i < itemNodes.getLength(); i++) {
            try {
                Element itemElement = (Element) itemNodes.item(i);
                RssItem rssItem = new RssItem();
                rssItem.setTitle(itemElement.getElementsByTagName("title").item(0).getTextContent());
                rssItem.setLink(itemElement.getElementsByTagName("link").item(0).getTextContent());
                rssItem.setDescription(itemElement.getElementsByTagName("description").item(0).getTextContent());
                rssItem.setPubDate(parsePubDate(
                    itemElement.getElementsByTagName("pubDate").item(0).getTextContent()));
                rssItems.add(rssItem);
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing séquentiel d'un item RSS: " + e.getMessage());
            }
        }
        return rssItems;
    }

    /**
     * Tâche Callable pour parser un item RSS individuellement
     */
    private static class ItemParserTask implements Callable<RssItem> {
        private final Element itemElement;

        public ItemParserTask(Element itemElement) {
            this.itemElement = itemElement;
        }

        @Override
        public RssItem call() throws Exception {
            RssItem rssItem = new RssItem();
            rssItem.setTitle(itemElement.getElementsByTagName("title").item(0).getTextContent());
            rssItem.setLink(itemElement.getElementsByTagName("link").item(0).getTextContent());
            rssItem.setDescription(itemElement.getElementsByTagName("description").item(0).getTextContent());
            rssItem.setPubDate(parsePubDate(
                itemElement.getElementsByTagName("pubDate").item(0).getTextContent()));
            return rssItem;
        }
    }

    /**
     * Télécharge et parse un document XML avec les paramètres de configuration
     */
    private Document downloadXmlDocument(String url) throws IOException, ParserConfigurationException, SAXException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        // Configurer la connexion selon les paramètres
        connection.setConnectTimeout(config.getConnectTimeout());
        connection.setReadTimeout(config.getReadTimeout());
        connection.setRequestProperty("User-Agent", config.getUserAgent());

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(connection.getInputStream());
            doc.getDocumentElement().normalize();
            return doc;
        } finally {
            connection.disconnect();
        }
    }

    public RssFeed ReadFeed(String url) throws ParserConfigurationException {
        // Utiliser le cache pour éviter les téléchargements répétés
        return cache.get(url, () -> {
            try {
                return parseFeed(url);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private RssFeed parseFeed(String url) throws ParserConfigurationException, SAXException, IOException {
        RssFeed rssFeed = null;

        try {
            // 1. Télécharger le flux XML
            Document doc = downloadXmlDocument(url);

            // 2. Parser avec DOM et getElementsByTagName
            Element channelElement = (Element) doc.getElementsByTagName("channel").item(0);
            String feedTitle = channelElement.getElementsByTagName("title").item(0).getTextContent();
            String feedLink = channelElement.getElementsByTagName("link").item(0).getTextContent();
            String feedDescription = channelElement.getElementsByTagName("description").item(0).getTextContent();
            NodeList itemNodes = channelElement.getElementsByTagName("item");

            // 3. Créer RssFeed et liste d'RssItem à partir du XML
            rssFeed = new RssFeed();
            rssFeed.setTitle(feedTitle);
            rssFeed.setLink(feedLink);
            rssFeed.setDescription(feedDescription);

            // Parser les items en parallèle pour de meilleures performances
            List<RssItem> rssItems = parseItemsInParallel(itemNodes);
            rssFeed.setItems(rssItems);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return rssFeed;
    }

    /**
     * Vide le cache des flux
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Supprime un flux spécifique du cache
     */
    public void removeFromCache(String url) {
        cache.remove(url);
    }

    /**
     * Retourne le nombre d'entrées en cache
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * Retourne la configuration actuelle
     */
    public Feed4jConfig getConfig() {
        return config;
    }
}
