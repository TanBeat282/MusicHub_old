package com.example.musichub.room_database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class SongEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String songId;
    private String title;
    private String artist;
    private String album;
    private int duration; // Duration in seconds
    private String jsonSong; // JSON representation of the song

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getJsonSong() {
        return jsonSong;
    }

    public void setJsonSong(String jsonSong) {
        this.jsonSong = jsonSong;
    }
}

