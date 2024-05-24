package com.example.musichub.room_database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "preferences")
public class PreferenceEntity {

    @NonNull
    @PrimaryKey
    private String type;

    private String jsonSong;

    // Getter and Setter for type
    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    // Getter and Setter for jsonSong
    public String getJsonSong() {
        return jsonSong;
    }

    public void setJsonSong(String jsonSong) {
        this.jsonSong = jsonSong;
    }
}
