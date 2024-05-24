package com.example.musichub.room_database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "song_positions")
public class SongPositionEntity {
    @PrimaryKey
    private int id = 1; // We assume there is only one current song position
    private int position;

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}