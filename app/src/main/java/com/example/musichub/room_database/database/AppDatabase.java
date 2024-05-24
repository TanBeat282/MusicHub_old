package com.example.musichub.room_database.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.musichub.room_database.dao.PlaylistDao;
import com.example.musichub.room_database.dao.PreferenceDao;
import com.example.musichub.room_database.dao.SongDao;
import com.example.musichub.room_database.dao.SongPositionDao;
import com.example.musichub.room_database.entity.PlaylistEntity;
import com.example.musichub.room_database.entity.PreferenceEntity;
import com.example.musichub.room_database.entity.SongEntity;
import com.example.musichub.room_database.entity.SongPositionEntity;

@Database(entities = {SongEntity.class, PlaylistEntity.class, SongPositionEntity.class, PreferenceEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
    public abstract SongPositionDao songPositionDao();
    public abstract PreferenceDao preferenceDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "musicHub.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

