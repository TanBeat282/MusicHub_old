package com.example.musichub.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musichub.room_database.entity.PlaylistEntity;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaylist(PlaylistEntity playlist);

    @Query("SELECT * FROM playlists WHERE name = :name")
    PlaylistEntity getPlaylistByName(String name);

    @Query("DELETE FROM playlists")
    void clearAllPlaylists();
}