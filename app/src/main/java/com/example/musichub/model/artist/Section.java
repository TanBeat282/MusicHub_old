package com.example.musichub.model.artist;

import com.example.musichub.model.chart.chart_home.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Section implements Serializable {
    private String sectionType;
    private String viewType;
    private String title;
    private String link;
    private ArrayList<Items> items;
    private String isOABrand;
    private String sectionId;
    private List<Integer> tabs;
    private boolean hasOA;

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

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public String getIsOABrand() {
        return isOABrand;
    }

    public void setIsOABrand(String isOABrand) {
        this.isOABrand = isOABrand;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public List<Integer> getTabs() {
        return tabs;
    }

    public void setTabs(List<Integer> tabs) {
        this.tabs = tabs;
    }

    public boolean isHasOA() {
        return hasOA;
    }

    public void setHasOA(boolean hasOA) {
        this.hasOA = hasOA;
    }
}
