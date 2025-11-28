# Feed4j - RSS Feed Parser for Java

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A high-performance, multi-threaded RSS feed parser library for Java that supports various RSS formats with intelligent caching and flexible configuration.

## 🚀 Features

- **Multi-threaded Parsing**: Parallel processing of RSS items for improved performance
- **Intelligent Caching**: Built-in cache system to avoid redundant downloads
- **Multiple Date Formats**: Supports RFC 1123, ISO, and various common date formats
- **Flexible Configuration**: Customizable timeouts, thread pools, and validation settings
- **Error Resilience**: Continues processing even if individual items fail
- **HTTP Configuration**: Custom User-Agent and timeout settings
- **XML Validation**: Optional XML validation support
- **Thread-Safe**: Safe for concurrent use

## 📦 Installation

### Maven

Not implemented.

### Manual Installation

1. Clone the repository
2. Build with Maven: `mvn clean install`
3. Add the generated JAR to your classpath

## 🔧 Quick Start

### Basic Usage

```java
import com.axeldev.Feed4j;
import com.axeldev.RssFeed;
import com.axeldev.RssItem;

// Create a parser with default configuration
Feed4j feed4j = new Feed4j();

// Parse an RSS feed
RssFeed feed = feed4j.ReadFeed("https://example.com/rss.xml");

if (feed != null) {
    System.out.println("Feed Title: " + feed.getTitle());
    System.out.println("Feed Description: " + feed.getDescription());

    for (RssItem item : feed.getItems()) {
        System.out.println("Title: " + item.getTitle());
        System.out.println("Link: " + item.getLink());
        System.out.println("Published: " + item.getPubDate());
        System.out.println("---");
    }
}
```

### Advanced Configuration

```java
import com.axeldev.Feed4jConfig;

// Create custom configuration
Feed4jConfig config = new Feed4jConfig()
    .setMaxThreads(8)                    // Max threads for parallel parsing
    .setConnectTimeout(15000)            // 15 second connection timeout
    .setReadTimeout(45000)               // 45 second read timeout
    .setUserAgent("MyApp/1.0")           // Custom User-Agent
    .setCacheDurationMs(600000)          // 10 minute cache duration
    .setValidateXml(false);              // Disable XML validation

// Create parser with custom configuration
Feed4j feed4j = new Feed4j(config);
RssFeed feed = feed4j.ReadFeed("https://example.com/rss.xml");
```

## 📚 API Reference

### Feed4j

The main parser class with the following constructors:

```java
// Default configuration
public Feed4j()

// Custom configuration
public Feed4j(Feed4jConfig config)
```

#### Methods

```java
// Parse an RSS feed
public RssFeed ReadFeed(String url)

// Cache management
public void clearCache()
public void removeFromCache(String url)
public int getCacheSize()

// Configuration access
public Feed4jConfig getConfig()
```

### Feed4jConfig

Configuration class with fluent API:

```java
public Feed4jConfig setMaxThreads(int maxThreads)
public Feed4jConfig setConnectTimeout(int connectTimeout)
public Feed4jConfig setReadTimeout(int readTimeout)
public Feed4jConfig setUserAgent(String userAgent)
public Feed4jConfig setValidateXml(boolean validateXml)
public Feed4jConfig setCacheDurationMs(long cacheDurationMs)
```

### RssFeed

Represents an RSS feed:

```java
public String getTitle()
public String getLink()
public String getDescription()
public List<RssItem> getItems()
```

### RssItem

Represents an individual RSS item:

```java
public String getTitle()
public String getLink()
public String getDescription()
public LocalDateTime getPubDate()
```

## 🎯 Supported Date Formats

Feed4j automatically parses various date formats commonly found in RSS feeds:

- RFC 1123: `EEE, dd MMM yyyy HH:mm:ss zzz`
- ISO Local: `yyyy-MM-dd'T'HH:mm:ss`
- Simplified: `yyyy-MM-dd HH:mm:ss`
- And other common variations

## ⚡ Performance Tips

1. **Use Appropriate Thread Count**: Set `maxThreads` based on your system's CPU cores
2. **Configure Timeouts**: Set reasonable timeouts to avoid hanging connections
3. **Enable Caching**: Use cache for frequently accessed feeds to reduce network calls
4. **Custom User-Agent**: Set a descriptive User-Agent to avoid being blocked by servers

## 🔧 Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `maxThreads` | CPU cores | Maximum threads for parallel parsing |
| `connectTimeout` | 10000ms | Connection timeout in milliseconds |
| `readTimeout` | 30000ms | Read timeout in milliseconds |
| `userAgent` | "Feed4j/1.0" | HTTP User-Agent header |
| `validateXml` | false | Enable XML validation |
| `cacheDurationMs` | 300000ms | Cache validity duration |

## 🛠️ Error Handling

Feed4j is designed to be resilient:

- Individual item parsing failures don't stop the entire feed processing
- Network errors are logged but don't crash the application
- Unsupported date formats are handled gracefully
- Cache failures fall back to fresh downloads

## 📝 Example Applications

### Simple RSS Reader

```java
public class RssReader {
    private final Feed4j feed4j;

    public RssReader() {
        Feed4jConfig config = new Feed4jConfig()
            .setCacheDurationMs(300000); // 5 minutes cache
        this.feed4j = new Feed4j(config);
    }

    public void displayLatestArticles(String feedUrl) {
        RssFeed feed = feed4j.ReadFeed(feedUrl);
        if (feed != null && feed.getItems() != null) {
            feed.getItems().stream()
                .limit(5) // Show only latest 5 items
                .forEach(item -> {
                    System.out.println(item.getTitle());
                    System.out.println(item.getLink());
                    System.out.println();
                });
        }
    }
}
```

### Feed Aggregator with Custom Configuration

```java
public class FeedAggregator {
    private final Feed4j feed4j;

    public FeedAggregator() {
        Feed4jConfig config = new Feed4jConfig()
            .setMaxThreads(Runtime.getRuntime().availableProcessors())
            .setConnectTimeout(20000)
            .setReadTimeout(60000)
            .setUserAgent("FeedAggregator/2.0");

        this.feed4j = new Feed4j(config);
    }

    public List<RssItem> aggregateFeeds(List<String> feedUrls) {
        return feedUrls.parallelStream()
            .map(feed4j::ReadFeed)
            .filter(Objects::nonNull)
            .flatMap(feed -> feed.getItems().stream())
            .sorted((a, b) -> b.getPubDate().compareTo(a.getPubDate()))
            .collect(Collectors.toList());
    }
}
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Axel** - [Your GitHub Profile](https://github.com/yourusername)

## 🙏 Acknowledgments

- Thanks to the Java community for the excellent XML parsing libraries
- Inspired by various RSS parsing libraries and real-world usage patterns

---

**Note**: This library is designed for parsing standard RSS 2.0 feeds. For Atom feeds or other formats, additional parsing logic may be required.
