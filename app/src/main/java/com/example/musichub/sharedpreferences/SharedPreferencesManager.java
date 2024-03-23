package com.example.musichub.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musichub.model.Artist;
import com.example.musichub.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private final Context context;

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    public void saveSongState(Song song) {
        if (song != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("song_id", song.getId());
            editor.putString("song_name", song.getName());
            editor.putString("song_artist", song.getArtist());
            editor.putString("song_thumb", song.getThumb());
            editor.putString("song_thumb_medium", song.getThumb_medium());
            editor.putString("lyric_song", song.getLyric());
            editor.putString("link_audio", song.getLink_audio());
            editor.putString("code", song.getCode());
            Gson gson = new Gson();
            String json = gson.toJson(song.getmArtist());
            editor.putString("artist", json);
            editor.apply();
        }
    }

    public Song restoreSongState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        String songId = sharedPreferences.getString("song_id", "");
        String songName = sharedPreferences.getString("song_name", "");
        String songArtist = sharedPreferences.getString("song_artist", "");
        String songThumb = sharedPreferences.getString("song_thumb", "");
        String songThumbMedium = sharedPreferences.getString("song_thumb_medium", "");
        String songLyric = sharedPreferences.getString("lyric_song", "");
        String linkAudio = sharedPreferences.getString("link_audio", "");
        String code = sharedPreferences.getString("code", "");
        String artistJson = sharedPreferences.getString("artist", "");

        Song restoredSong = new Song();
        restoredSong.setId(songId);
        restoredSong.setName(songName);
        restoredSong.setArtist(songArtist);
        restoredSong.setThumb(songThumb);
        restoredSong.setThumb_medium(songThumbMedium);
        restoredSong.setLyric(songLyric);
        restoredSong.setLink_audio(linkAudio);
        restoredSong.setCode(code);

        // Giải mã chuỗi JSON để khôi phục lại trạng thái của đối tượng Artist
        Gson gson = new Gson();
        Artist restoredArtist = gson.fromJson(artistJson, Artist.class);
        restoredSong.setmArtist(restoredArtist);

        return restoredSong;
    }


    public void saveSongArrayList(ArrayList<Song> songArrayList) {
        Gson gson = new Gson();
        String songListJson = gson.toJson(songArrayList);

        SharedPreferences sharedPreferences = context.getSharedPreferences("songList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_list", songListJson);
        editor.apply();
    }

    public ArrayList<Song> restoreSongArrayList() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("songList", Context.MODE_PRIVATE);
        String song_list = sharedPreferences.getString("song_list", "");

        ArrayList<Song> restoreSongList = gson.fromJson(song_list, new TypeToken<ArrayList<Song>>() {
        }.getType());
        return new ArrayList<Song>(restoreSongList);
    }

    //history song
    public ArrayList<Song> restoreSongArrayListHistory() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("songListHistory", Context.MODE_PRIVATE);
        String song_list = sharedPreferences.getString("song_list_history", "");

        ArrayList<Song> restoreSongList = gson.fromJson(song_list, new TypeToken<ArrayList<Song>>() {
        }.getType());
        if (restoreSongList == null) {
            restoreSongList = new ArrayList<>();
        }
        return restoreSongList;
    }


    public void saveSongArrayListHistory(Song song) {
        ArrayList<Song> songArrayList = restoreSongArrayListHistory();
        if (songArrayList == null) {
            songArrayList = new ArrayList<>();
        }

        for (int i = 0; i < songArrayList.size(); i++) {
            if (songArrayList.get(i).getId().equals(song.getId())) { // Kiểm tra xem ID của bài hát có trùng không
                songArrayList.remove(i); // Xóa bài hát nếu trùng
            }
        }
        songArrayList.add(0, song); // Thêm bài hát vào đầu danh sách

        Gson gson = new Gson();
        String songListJson = gson.toJson(songArrayList);

        SharedPreferences sharedPreferences = context.getSharedPreferences("songListHistory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_list_history", songListJson);
        editor.apply();
    }


    //positon song
    public void saveSongPosition(int positionSong) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("songPosition", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("position_song", positionSong);
        editor.apply();
    }

    public int restoreSongPosition() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("songPosition", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("position_song", 0);
    }

    public void saveActionState(int action) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_action", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("action", action).apply();
    }

    public int restoreActionState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_action", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("action", 0);
    }

    public void saveIsPlayState(boolean is_play) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_play", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_play", is_play).apply();
    }

    public boolean restoreIsPlayState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_play", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_play", false);
    }

    public void saveColorBackgroundState(int color_background, int color_bottomsheet) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_color", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("color_background", color_background);
        editor.putInt("color_bottomsheet", color_bottomsheet).apply();
    }

    public int[] restoreColorBackgrounState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_color", Context.MODE_PRIVATE);
        return new int[]{sharedPreferences.getInt("color_background", 0), sharedPreferences.getInt("color_bottomsheet", 0)};
    }

    public void clearAll() {
        SharedPreferences musicPrefs = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        SharedPreferences musicActionPrefs = context.getSharedPreferences("music_action", Context.MODE_PRIVATE);
        SharedPreferences musicIsPlayPrefs = context.getSharedPreferences("music_is_play", Context.MODE_PRIVATE);

        SharedPreferences.Editor musicPrefsEditor = musicPrefs.edit();
        SharedPreferences.Editor musicActionPrefsEditor = musicActionPrefs.edit();
        SharedPreferences.Editor musicIsPlayPrefsEditor = musicIsPlayPrefs.edit();

        musicPrefsEditor.clear().apply();
        musicActionPrefsEditor.clear().apply();
        musicIsPlayPrefsEditor.clear().apply();
    }

}
