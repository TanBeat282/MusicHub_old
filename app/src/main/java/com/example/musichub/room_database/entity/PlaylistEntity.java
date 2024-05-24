package com.example.musichub.room_database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlists")
public class PlaylistEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String jsonPlaylist; // JSON representation of the playlist

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsonPlaylist() {
        return jsonPlaylist;
    }

    public void setJsonPlaylist(String jsonPlaylist) {
        this.jsonPlaylist = jsonPlaylist;
    }
}
