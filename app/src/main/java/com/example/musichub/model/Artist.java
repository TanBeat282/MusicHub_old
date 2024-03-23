package com.example.musichub.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String link;
    private String cover;
    private String thumbnail;

    public Artist() {
    }

    public Artist(String id, String name, String link, String cover, String thumbnail) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.cover = cover;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
