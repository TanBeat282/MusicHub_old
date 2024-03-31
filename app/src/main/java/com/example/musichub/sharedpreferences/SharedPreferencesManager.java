package com.example.musichub.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.musichub.model.chart.chart_home.Items;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SharedPreferencesManager {
    private final Context context;

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    public void saveSongState(Items song) {
        if (song != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            String jsonSong = gson.toJson(song);

            editor.putString("song", jsonSong);
            editor.apply();
        }
    }
    public Items restoreSongState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        String jsonSong = sharedPreferences.getString("song", null);

        if (jsonSong != null) {
            Gson gson = new Gson();
            return gson.fromJson(jsonSong, Items.class);
        } else {
            return null;
        }
    }

    public void saveSongArrayList(ArrayList<Items> songArrayList) {
        Gson gson = new Gson();
        String songListJson = gson.toJson(songArrayList);

        SharedPreferences sharedPreferences = context.getSharedPreferences("songList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_list", songListJson);
        editor.apply();
    }

    public ArrayList<Items> restoreSongArrayList() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("songList", Context.MODE_PRIVATE);
        String song_list = sharedPreferences.getString("song_list", "");

        ArrayList<Items> restoreSongList = gson.fromJson(song_list, new TypeToken<ArrayList<Items>>() {
        }.getType());
        return new ArrayList<Items>(restoreSongList);
    }

    //history song
    public ArrayList<Items> restoreSongArrayListHistory() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("songListHistory", Context.MODE_PRIVATE);
        String song_list = sharedPreferences.getString("song_list_history", "");

        ArrayList<Items> restoreSongList = gson.fromJson(song_list, new TypeToken<ArrayList<Items>>() {
        }.getType());
        if (restoreSongList == null) {
            restoreSongList = new ArrayList<>();
        }
        return restoreSongList;
    }


    public void saveSongArrayListHistory(Items song) {
        ArrayList<Items> songArrayList = restoreSongArrayListHistory();
        if (songArrayList == null) {
            songArrayList = new ArrayList<>();
        }

        for (int i = 0; i < songArrayList.size(); i++) {
            if (songArrayList.get(i).getEncodeId().equals(song.getEncodeId())) { // Kiểm tra xem ID của bài hát có trùng không
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


    public void saveIsShuffleState(boolean is_shuffle) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_shuffle", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_shuffle", is_shuffle).apply();
    }

    public boolean restoreIsShuffleState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_shuffle", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_shuffle", false);
    }

    public void saveIsRepeatState(boolean is_repeat) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_repeat", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_repeat", is_repeat).apply();
    }

    public boolean restoreIsRepeatState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_repeat", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_repeat", false);
    }
    public void saveIsRepeatOneState(boolean is_repeat_one) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_repeat_one", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_repeat_one", is_repeat_one).apply();
    }

    public boolean restoreIsRepeatOneState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("music_is_repeat_one", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_repeat_one", false);
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
