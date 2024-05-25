package com.example.musichub.model.search;

import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.playlist.DataPlaylist;
import com.example.musichub.model.playlist.Playlist;

import java.io.Serializable;
import java.util.ArrayList;

public class DataSearch implements Serializable {
    private Items top;
    private ArrayList<Artists> artists;
    private ArrayList<Items> songs;
    private ArrayList<Video> videos;
    private ArrayList<DataPlaylist> playlists;
    private CounterSearch counter;
    private String sectionId;

    public Items getTop() {
        return top;
    }

    public void setTop(Items top) {
        this.top = top;
    }

    public ArrayList<Artists> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artists> artists) {
        this.artists = artists;
    }

    public ArrayList<Items> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Items> songs) {
        this.songs = songs;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

    public ArrayList<DataPlaylist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<DataPlaylist> playlists) {
        this.playlists = playlists;
    }

    public CounterSearch getCounter() {
        return counter;
    }

    public void setCounter(CounterSearch counter) {
        this.counter = counter;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
