package com.example.musichub.room_database;

import android.content.Context;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.room_database.dao.PlaylistDao;
import com.example.musichub.room_database.dao.PreferenceDao;
import com.example.musichub.room_database.dao.SongDao;
import com.example.musichub.room_database.dao.SongPositionDao;
import com.example.musichub.room_database.database.AppDatabase;
import com.example.musichub.room_database.entity.PlaylistEntity;
import com.example.musichub.room_database.entity.PreferenceEntity;
import com.example.musichub.room_database.entity.SongEntity;
import com.example.musichub.room_database.entity.SongPositionEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final SongDao songDao;
    private final PlaylistDao playlistDao;
    private final SongPositionDao songPositionDao;
    private final PreferenceDao preferenceDao;
    private final Gson gson;

    public DatabaseManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        songDao = db.songDao();
        playlistDao = db.playlistDao();
        songPositionDao = db.songPositionDao();
        preferenceDao = db.preferenceDao();
        gson = new Gson();
    }

    // Save song state
    public void saveSongState(Items song) {
        if (song != null) {
            String jsonSong = gson.toJson(song);
            SongEntity songEntity = new SongEntity();
            songEntity.setSongId(song.getEncodeId());
            songEntity.setTitle(song.getTitle());
            songEntity.setArtist(song.getArtist().getName());
            songEntity.setAlbum(song.getAlbum().getArtistsNames());
            songEntity.setDuration(song.getDuration());
            songEntity.setJsonSong(jsonSong);
            songDao.insertSong(songEntity);
        }
    }

    // Restore song state
    public Items restoreSongState(String songId) {
        SongEntity songEntity = songDao.getSongById(songId);
        if (songEntity != null) {
            return gson.fromJson(songEntity.getJsonSong(), Items.class);
        }
        return null;
    }

    // Save playlist
    public void savePlaylist(String name, List<Items> playlist) {
        if (playlist != null) {
            String jsonPlaylist = gson.toJson(playlist);
            PlaylistEntity playlistEntity = new PlaylistEntity();
            playlistEntity.setName(name);
            playlistEntity.setJsonPlaylist(jsonPlaylist);
            playlistDao.insertPlaylist(playlistEntity);
        }
    }

    // Restore playlist
    public List<Items> restorePlaylist(String name) {
        PlaylistEntity playlistEntity = playlistDao.getPlaylistByName(name);
        if (playlistEntity != null) {
            return gson.fromJson(playlistEntity.getJsonPlaylist(), new TypeToken<List<Items>>(){}.getType());
        }
        return null;
    }

    // Save song position
    public void saveSongPosition(int positionSong) {
        SongPositionEntity songPositionEntity = new SongPositionEntity();
        songPositionEntity.setPosition(positionSong);
        songPositionDao.insertSongPosition(songPositionEntity);
    }

    // Restore song position
    public int restoreSongPosition() {
        SongPositionEntity songPositionEntity = songPositionDao.getSongPosition();
        return songPositionEntity != null ? songPositionEntity.getPosition() : 0;
    }

    // Save preference (generic method for other states)
    public void savePreference(String type, String value) {
        PreferenceEntity preferenceEntity = new PreferenceEntity();
        preferenceEntity.setType(type);
        preferenceEntity.setJsonSong(value);
        preferenceDao.insertPreference(preferenceEntity);
    }

    // Restore preference
    public String restorePreference(String type, String defaultValue) {
        PreferenceEntity preferenceEntity = preferenceDao.getPreferenceByType(type);
        return preferenceEntity != null ? preferenceEntity.getJsonSong() : defaultValue;
    }

    // Clear all data

    public void clearAll() {
        songDao.clearAllSongs();
        playlistDao.clearAllPlaylists();
    }
}

