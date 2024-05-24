package com.example.musichub.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musichub.room_database.entity.PreferenceEntity;

@Dao
public interface PreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPreference(PreferenceEntity preference);

    @Query("SELECT * FROM preferences WHERE type = :type")
    PreferenceEntity getPreferenceByType(String type);

    @Query("DELETE FROM preferences WHERE type = :type")
    void deletePreference(String type);
}
