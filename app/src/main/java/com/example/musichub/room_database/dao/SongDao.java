package com.example.musichub.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musichub.room_database.entity.SongEntity;

import java.util.List;

@Dao
public interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(SongEntity song);

    @Query("SELECT * FROM songs WHERE songId = :songId")
    SongEntity getSongById(String songId);

    @Query("DELETE FROM songs")
    void clearAllSongs();
}