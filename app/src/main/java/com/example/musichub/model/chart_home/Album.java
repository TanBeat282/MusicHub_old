package com.example.musichub.model.chart_home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private String encodeId;
    private String title;
    private String thumbnail;
    private boolean isOffical;
    private String link;
    private boolean isIndie;
    private String releaseDate;
    private String sortDescription;
    private long releasedAt;
    private ArrayList<String> genreIds;
    private boolean PR;
    private ArrayList<Artists> artists;
    private String artistsNames;

    public String getEncodeId() {
        return encodeId;
    }

    public void setEncodeId(String encodeId) {
        this.encodeId = encodeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isOffical() {
        return isOffical;
    }

    public void setOffical(boolean offical) {
        isOffical = offical;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isIndie() {
        return isIndie;
    }

    public void setIndie(boolean indie) {
        isIndie = indie;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSortDescription() {
        return sortDescription;
    }

    public void setSortDescription(String sortDescription) {
        this.sortDescription = sortDescription;
    }

    public long getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(long releasedAt) {
        this.releasedAt = releasedAt;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(ArrayList<String> genreIds) {
        this.genreIds = genreIds;
    }

    public boolean isPR() {
        return PR;
    }

    public void setPR(boolean PR) {
        this.PR = PR;
    }

    public List<Artists> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artists> artists) {
        this.artists = artists;
    }

    public String getArtistsNames() {
        return artistsNames;
    }

    public void setArtistsNames(String artistsNames) {
        this.artistsNames = artistsNames;
    }
}
