package com.example.musichub.model.search;

import com.example.musichub.model.chart_home.Artists;
import com.example.musichub.model.chart_home.Items;

import java.io.Serializable;
import java.util.ArrayList;

public class DataSearch implements Serializable {
    private TopSearch top;
    private ArrayList<Artists> artists;
    private ArrayList<Items> songs;
    private ArrayList<Video> videos;
    private ArrayList<Playlist> playlists;

    public TopSearch getTop() {
        return top;
    }

    public void setTop(TopSearch top) {
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

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
}
