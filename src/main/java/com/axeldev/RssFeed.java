package com.axeldev;

import java.util.List;

public class RssFeed {
    private String title;
    private String link;
    private String description;
    private List<RssItem> items;

    public String getTitle() {
        return title;
    }
    public String getLink() {
        return link;
    }
    public String getDescription() {
        return description;
    }
    public List<RssItem> getItems() {
        return items;
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
    public void setItems(List<RssItem> items) {
        this.items = items;
    }
}
