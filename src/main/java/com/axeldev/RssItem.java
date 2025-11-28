package com.axeldev;

import java.time.LocalDateTime;

public class RssItem {
    private String title;
    private String link;
    private String description;
    private LocalDateTime pubDate;

    public String getTitle() {
        return title;
    }
    public String getLink() {
        return link;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }
}
