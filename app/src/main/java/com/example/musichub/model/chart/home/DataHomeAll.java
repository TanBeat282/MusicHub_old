package com.example.musichub.model.chart.home;

import java.io.Serializable;
import java.util.ArrayList;

public class DataHomeAll implements Serializable {
    private String sectionType;
    private String viewType;
    private String title;
    private String link;
    private String sectionId;
    private ItemsData items;

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public ItemsData getItems() {
        return items;
    }

    public void setItems(ItemsData items) {
        this.items = items;
    }
}
