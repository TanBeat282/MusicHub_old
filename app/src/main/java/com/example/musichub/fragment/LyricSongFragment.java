package com.example.musichub.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.musichub.R;
import com.example.musichub.adapter.LyricsAdapter;
import com.example.musichub.model.LyricLine;
import com.example.musichub.model.Song;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LyricSongFragment extends Fragment {
    private RecyclerView recyclerViewLyrics;
    private LinearLayout txtNoData;
    private LyricsAdapter lyricsAdapter;
    private List<LyricLine> lyrics;
    private Handler lyricHandler;
    private ExecutorService executor;
    private Handler handler;
    private SharedPreferencesManager sharedPreferencesManager;
    private Song song;
    private int currentTime, total_time;
    private boolean isPlaying;
    private int action;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            song = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            action = bundle.getInt("action_music");
            getDataSong(song);
        }
    };

    private final BroadcastReceiver seekBarUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentTime = intent.getIntExtra("current_time", 0);
            total_time = intent.getIntExtra("total_time", 0);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lyric_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        song = sharedPreferencesManager.restoreSongState();

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        lyrics = new ArrayList<>(); // hoặc lyrics = new ArrayList<>(danh_sach_lyrics_da_co);

        recyclerViewLyrics = view.findViewById(R.id.recyclerViewLyrics);
        txtNoData = view.findViewById(R.id.txtNoData);

        recyclerViewLyrics.setLayoutManager(new LinearLayoutManager(getContext()));

        lyricsAdapter = new LyricsAdapter(getContext(), lyrics);
        recyclerViewLyrics.setAdapter(lyricsAdapter);

        recyclerViewLyrics.setVisibility(View.GONE);
        txtNoData.setVisibility(View.VISIBLE);

        // Khởi tạo danh sách lyricLines và lyricHandler
        lyrics = new ArrayList<>();
        lyricHandler = new Handler();

        getDataSong(song);
    }

    private class GetLyricTask implements Runnable {
        private final String url;

        public GetLyricTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();
                connection.disconnect();
                final String lyricContent = result.toString();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        lyrics = parseLyrics(lyricContent);
                        Runnable updateLyricRunnable = new Runnable() {
                            @Override
                            public void run() {
                                updateLyricDisplay();
                                lyricHandler.postDelayed(this, 500);
                            }
                        };
                        lyricHandler.post(updateLyricRunnable);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startGetLyricTask(String url) {
        executor.execute(new GetLyricTask(url));
    }


    private void updateLyricDisplay() {
        if (lyrics != null && !lyrics.isEmpty()) {

            int currentLineIndex = -1;
            for (int i = 0; i < lyrics.size(); i++) {
                LyricLine currentLine = lyrics.get(i);
                if (currentTime >= currentLine.getStartTime() - 1000) {
                    currentLineIndex = i;
                } else {
                    break;
                }
            }

            lyricsAdapter.updateLyricLines(lyrics);
            lyricsAdapter.setCurrentPlaybackTime(currentTime);

            // Kiểm tra nếu có item trùng với currentTime
            if (currentLineIndex != -1) {
                recyclerViewLyrics.smoothScrollToPosition(currentLineIndex + 8);
            }

        }
    }

    private List<LyricLine> parseLyrics(String lyricContent) {
        List<LyricLine> lyricLines = new ArrayList<>();

        String[] lines = lyricContent.split("\n");
        for (String line : lines) {
            String regex = "\\[(\\d{2}):(\\d{2}).(\\d{2})](.*)";
            if (line.matches(regex)) {
                String minuteStr = line.replaceAll(regex, "$1");
                String secondStr = line.replaceAll(regex, "$2");
                String millisecondStr = line.replaceAll(regex, "$3");
                String content = line.replaceAll(regex, "$4");

                int minute = Integer.parseInt(minuteStr);
                int second = Integer.parseInt(secondStr);
                int millisecond = Integer.parseInt(millisecondStr);
                int startTime = minute * 60 * 1000 + second * 1000 + millisecond * 10;

                lyricLines.add(new LyricLine(startTime, content));
            }
        }

        return lyricLines;
    }

    private void getDataSong(Song song) {
        if (song != null) {
            if (!song.getLyric().isEmpty() && !song.getLyric().equals(" ")) {
                startGetLyricTask(song.getLyric());
                recyclerViewLyrics.setVisibility(View.VISIBLE);
                txtNoData.setVisibility(View.GONE);
            } else {
                recyclerViewLyrics.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(seekBarUpdateReceiver, new IntentFilter("send_seekbar_update"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(seekBarUpdateReceiver);
    }
}