package com.example.musichub.room_database.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musichub.room_database.entity.SongPositionEntity;

@Dao
public interface SongPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongPosition(SongPositionEntity songPosition);

    @Query("SELECT * FROM song_positions WHERE id = 1")
    SongPositionEntity getSongPosition();
}