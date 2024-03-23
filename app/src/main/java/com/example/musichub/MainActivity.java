package com.example.musichub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;

import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.activity.SearchActivity;
import com.example.musichub.adapter.BaiHatNhanhAdapter;
import com.example.musichub.adapter.LichSuBaiHatAdapter;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.adapter.VideoAdapter;
import com.example.musichub.helper.Helper;
import com.example.musichub.model.Artist;
import com.example.musichub.model.Song;
import com.example.musichub.service.MyService;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.FormatField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private ImageView img_play_pause;
    private RoundedImageView img_album_song;
    private LinearLayout layoutPlayer, linear_play_pause, linear_next, txtNoData;
    private TextView tvTitleSong, tvSingleSong;
    private RecyclerView rv_nghe_lai;
    private LinearProgressIndicator progressIndicator;
    private Song mSong;
    private boolean isPlaying;
    private int action;
    private int currentTime, total_time;
    private TopSongAdapter topSongAdapter;
    private BaiHatNhanhAdapter baiHatNhanhAdapter;
    private LichSuBaiHatAdapter lichSuBaiHatAdapter;
    private VideoAdapter videoAdapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private View layoutPlayerBottom;
    private ArrayList<Song> songListVideoBaiHat;
    private ArrayList<Song> songListLichSuBaiHat;

    private final YoutubeDownloader downloader = new YoutubeDownloader();

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            mSong = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            action = bundle.getInt("action_music");
            if (action == MyService.ACTION_START || action == MyService.ACTION_NEXT || action == MyService.ACTION_PREVIOUS) {
                setBackgroundBottomPlayer();
            }
            handleLayoutMusic(action);
        }
    };
    private final BroadcastReceiver seekBarUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentTime = intent.getIntExtra("current_time", 0);
            total_time = intent.getIntExtra("total_time", 0);
            updateIndicator(currentTime, total_time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());

        RecyclerView rv_chon_nhanh = findViewById(R.id.rv_chon_nhanh);
        RecyclerView rv_bang_xep_hang = findViewById(R.id.rv_bang_xep_hang);
        rv_nghe_lai = findViewById(R.id.rv_nghe_lai);
        RecyclerView rv_video_bai_hat_lien_quan = findViewById(R.id.rv_video_bai_hat_lien_quan);


        ImageView img_search = findViewById(R.id.img_search);
        layoutPlayerBottom = findViewById(R.id.layoutPlayerBottom);
        txtNoData = findViewById(R.id.txtNoData);

        layoutPlayer = layoutPlayerBottom.findViewById(R.id.layoutPlayer);
        linear_play_pause = layoutPlayerBottom.findViewById(R.id.linear_play_pause);
        img_play_pause = layoutPlayerBottom.findViewById(R.id.img_play_pause);

        linear_next = layoutPlayerBottom.findViewById(R.id.linear_next);

        img_album_song = layoutPlayerBottom.findViewById(R.id.img_album_song);
        tvTitleSong = layoutPlayerBottom.findViewById(R.id.txtTile);
        tvTitleSong.setSelected(true);
        tvSingleSong = layoutPlayerBottom.findViewById(R.id.txtArtist);
        tvSingleSong.setSelected(true);
        progressIndicator = layoutPlayerBottom.findViewById(R.id.progressIndicator);

        ArrayList<Song> songListChonNhanh = new ArrayList<>();
        ArrayList<Song> songListBangXepHang = new ArrayList<>();
        songListLichSuBaiHat = new ArrayList<>();
        songListVideoBaiHat = new ArrayList<>();


        // Khoi tạo RecyclerView và Adapter
        SnapHelper snapHelperChonNhanh = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null) {
                    return RecyclerView.NO_POSITION;
                }
                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }
                if (layoutManager instanceof GridLayoutManager) {
                    if (targetPosition == -1 || targetPosition >= layoutManager.getItemCount()) {
                        return RecyclerView.NO_POSITION;
                    }
                }
                return targetPosition;
            }
        };
        snapHelperChonNhanh.attachToRecyclerView(rv_chon_nhanh);

        GridLayoutManager layoutManagerChonNhanh = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_chon_nhanh.setLayoutManager(layoutManagerChonNhanh);


        SnapHelper snapHelperBangXepHang = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null) {
                    return RecyclerView.NO_POSITION;
                }
                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }
                if (layoutManager instanceof GridLayoutManager) {
                    if (targetPosition == -1 || targetPosition >= layoutManager.getItemCount()) {
                        return RecyclerView.NO_POSITION;
                    }
                }
                return targetPosition;
            }
        };
        snapHelperBangXepHang.attachToRecyclerView(rv_bang_xep_hang);

        GridLayoutManager layoutManagerBangXepHang = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_bang_xep_hang.setLayoutManager(layoutManagerBangXepHang);


        SnapHelper snapHelperLichSuBaiHat = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null) {
                    return RecyclerView.NO_POSITION;
                }
                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }
                if (layoutManager instanceof GridLayoutManager) {
                    if (targetPosition == -1 || targetPosition >= layoutManager.getItemCount()) {
                        return RecyclerView.NO_POSITION;
                    }
                }
                return targetPosition;
            }
        };
        snapHelperLichSuBaiHat.attachToRecyclerView(rv_nghe_lai);

        GridLayoutManager layoutManagerLichSuBaiHat = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_nghe_lai.setLayoutManager(layoutManagerLichSuBaiHat);


        SnapHelper snapHelperVideoBaiHat = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null) {
                    return RecyclerView.NO_POSITION;
                }
                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }
                if (layoutManager instanceof GridLayoutManager) {
                    if (targetPosition == -1 || targetPosition >= layoutManager.getItemCount()) {
                        return RecyclerView.NO_POSITION;
                    }
                }
                return targetPosition;
            }
        };
        snapHelperVideoBaiHat.attachToRecyclerView(rv_video_bai_hat_lien_quan);

        GridLayoutManager layoutManagerVideoBaiHat = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_video_bai_hat_lien_quan.setLayoutManager(layoutManagerVideoBaiHat);


        // Khoi tạo Adapter
        baiHatNhanhAdapter = new BaiHatNhanhAdapter(songListChonNhanh, MainActivity.this);
        rv_chon_nhanh.setAdapter(baiHatNhanhAdapter);


        topSongAdapter = new TopSongAdapter(songListBangXepHang, MainActivity.this);
        rv_bang_xep_hang.setAdapter(topSongAdapter);


        videoAdapter = new VideoAdapter(songListVideoBaiHat, MainActivity.this);
        rv_video_bai_hat_lien_quan.setAdapter(videoAdapter);


        lichSuBaiHatAdapter = new LichSuBaiHatAdapter(songListLichSuBaiHat, MainActivity.this);
        rv_nghe_lai.setAdapter(lichSuBaiHatAdapter);

        rv_nghe_lai.setVisibility(View.GONE);
        txtNoData.setVisibility(View.VISIBLE);

        getSongCurrent();
        getBangXepHang();
        getSongHistory();
        setBackgroundBottomPlayer();

//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        layoutPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayNowActivity.class);
            startActivity(intent);
        });
        img_search.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    private void getBangXepHang() {
        OkHttpClient client = new OkHttpClient();

        // Tạo request để gửi lời gọi HTTP GET đến URL
        String url = "https://mp3.zing.vn/xhr/chart-realtime";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String jsonString = responseBody.string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            JSONArray songArray = dataObject.getJSONArray("song");

                            ArrayList<Song> filteredSongList = new ArrayList<>();

                            for (int i = 0; i < songArray.length(); i++) {

                                JSONObject songObject = songArray.getJSONObject(i);

                                Song song = new Song();

                                //convert background
                                String temp = songObject.getString("thumbnail");
                                String prefix = "https://photo-resize-zmp3.zmdcdn.me/w94_r1x1_jpeg/";
                                String urlThumb = "https://photo-resize-zmp3.zmdcdn.me/w480_r1x1_png/";

                                // Tìm vị trí bắt đầu của Chan giá trị cần lấy
                                int startIndex = temp.indexOf(prefix) + prefix.length();

                                song.setThumb_medium(temp);
                                song.setLyric(songObject.getString("lyric"));
                                song.setThumb(urlThumb + temp.substring(startIndex));

                                song.setArtist(songObject.getString("artists_names"));
                                song.setId(songObject.getString("id"));
                                song.setName(songObject.getString("name"));
                                song.setCode(songObject.getString("code"));

                                getLinkAudioSong(song.getCode(), link -> {
                                    if (link != null) {
                                        song.setLink_audio(link);
                                    } else {
                                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi truy vấn.", Toast.LENGTH_SHORT).show());
                                    }
                                });
                                filteredSongList.add(song);
                            }
                            runOnUiThread(() -> topSongAdapter.setFilterList(filteredSongList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    private void handleLayoutMusic(int action) {
        switch (action) {
            case MyService.ACTION_START:
                layoutPlayerBottom.setVisibility(View.VISIBLE);
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
                layoutPlayerBottom.setVisibility(View.VISIBLE);
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_RESUME:
                layoutPlayerBottom.setVisibility(View.VISIBLE);
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                layoutPlayerBottom.setVisibility(View.GONE);
                break;
        }
    }

    private void showInfoSong() {
        if (mSong == null) {
            return;
        }

        Glide.with(this)
                .load(mSong.getThumb_medium())
                .into(img_album_song);
        tvTitleSong.setText(mSong.getName());
        tvSingleSong.setText(mSong.getArtist());

        linear_play_pause.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(MainActivity.this, MyService.class)) {
                startService(new Intent(this, MyService.class));
            }
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
            }
        });
        linear_next.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(MainActivity.this, MyService.class)) {
                startService(new Intent(MainActivity.this, MyService.class));
            }
            sendActionToService(MyService.ACTION_NEXT);
        });
    }

    private void setStatusButtonPlayOrPause() {
        if (!Helper.isMyServiceRunning(MainActivity.this, MyService.class)) {
            isPlaying = false;
        }
        if (isPlaying) {
            img_play_pause.setImageResource(R.drawable.baseline_pause_24);
        } else {
            img_play_pause.setImageResource(R.drawable.baseline_play_arrow_24);

        }
    }

    private void setBackgroundBottomPlayer() {
        int[] colors = sharedPreferencesManager.restoreColorBackgrounState();
        int color_background = colors[0];

        if (color_background != 0) {
            ColorStateList colorStateList = ColorStateList.valueOf(color_background);
            ViewCompat.setBackgroundTintList(layoutPlayer, colorStateList);
        }
    }

    private void updateIndicator(int currentTime, int totalTime) {
        if (totalTime > 0) {
            float progress = (float) currentTime / totalTime;
            int progressInt = (int) (progress * 100);
            progressIndicator.setProgressCompat(progressInt, true);
        }
    }


    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }

    private void getSongCurrent() {
        mSong = sharedPreferencesManager.restoreSongState();
        isPlaying = sharedPreferencesManager.restoreIsPlayState();
        action = sharedPreferencesManager.restoreActionState();
        getBaiHatNhanh(mSong);
        new SearchTask().execute();
        handleLayoutMusic(action);
    }

    private void getSongHistory() {
        songListLichSuBaiHat = sharedPreferencesManager.restoreSongArrayListHistory();
        if (songListLichSuBaiHat.isEmpty()) {
            rv_nghe_lai.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            rv_nghe_lai.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            lichSuBaiHatAdapter.setFilterList(songListLichSuBaiHat);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class SearchTask extends AsyncTask<Void, Void, SearchResult> {
        @Override
        protected SearchResult doInBackground(Void... voids) {
            RequestSearchResult request = new RequestSearchResult(mSong.getName())
                    .filter(
                            TypeField.VIDEO,
                            FormatField.HD);

            return downloader.search(request).data();
        }

        @Override
        protected void onPostExecute(SearchResult result) {
            List<SearchResultVideoDetails> videos = result.videos();

            for (SearchResultVideoDetails searchResultVideoDetails : videos) {
                new GetVideoInfoTask().execute(searchResultVideoDetails.videoId());
            }
        }
    }

    private class GetVideoInfoTask extends AsyncTask<String, Void, VideoInfo> {
        @Override
        protected VideoInfo doInBackground(String... videoIds) {
            RequestVideoInfo request = new RequestVideoInfo(videoIds[0]);
            com.github.kiulian.downloader.downloader.response.Response<VideoInfo> response = downloader.getVideoInfo(request);
            VideoInfo info = response.data();
            return info;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(VideoInfo info) {
            VideoDetails details = info.details();
            List<VideoWithAudioFormat> videoWithAudioFormats = info.videoWithAudioFormats();

            Song song = new Song();
            song.setId(details.videoId());
            song.setArtist(String.valueOf(details.viewCount()));
            song.setThumb_medium(details.thumbnails().get(0));
            song.setThumb(details.thumbnails().get(0));
            song.setName(details.title());
            song.setLyric(null);
            song.setLink_audio(videoWithAudioFormats.get(1).url());

            songListVideoBaiHat.add(song);
            videoAdapter.notifyDataSetChanged();

        }

    }

    private void getBaiHatNhanh(Song song) {
        if (song != null) {

            OkHttpClient client = new OkHttpClient();
            String url = " http://mp3.zing.vn/xhr/recommend?type=audio&id=" + song.getId();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            String jsonString = responseBody.string();
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                JSONArray songArray = dataObject.getJSONArray("items");

                                ArrayList<Song> filteredSongList = new ArrayList<>();

                                for (int i = 0; i < songArray.length(); i++) {

                                    JSONObject songObject = songArray.getJSONObject(i);

                                    Song song = new Song();

                                    //convert background
                                    String temp = songObject.getString("thumbnail");
                                    String prefix = "https://photo-resize-zmp3.zmdcdn.me/w94_r1x1_jpeg/";
                                    String urlThumb = "https://photo-resize-zmp3.zmdcdn.me/w480_r1x1_png/";

                                    // Tìm vị trí bắt đầu của Chan giá trị cần lấy
                                    int startIndex = temp.indexOf(prefix) + prefix.length();

                                    song.setThumb_medium(songObject.getString("thumbnail"));
                                    song.setLyric(songObject.getString("lyric"));
                                    song.setThumb(urlThumb + temp.substring(startIndex));

                                    song.setArtist(songObject.getString("performer"));
                                    song.setId(songObject.getString("id"));
                                    song.setName(songObject.getString("name"));
                                    song.setCode(songObject.getString("code"));

                                    getLinkAudioSong(song.getCode(), new LinkAudioCallback() {
                                        @Override
                                        public void onLinkReceived(String link) {
                                            if (link != null) {
                                                song.setLink_audio(link);
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(MainActivity.this, "Lỗi truy vấn.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    filteredSongList.add(song);
                                }
                                runOnUiThread(() -> baiHatNhanhAdapter.setFilterList(filteredSongList));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    public interface LinkAudioCallback {
        void onLinkReceived(String link);
    }


    private void getLinkAudioSong(String code_song, final LinkAudioCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Tạo request để gửi lời gọi HTTP GET đến URL
        String url = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + code_song;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onLinkReceived(null); // Gọi callback với giá trị null khi xảy ra lỗi
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String jsonString = responseBody.string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            JSONObject sourceObject = dataObject.getJSONObject("source");
                            String link128 = sourceObject.getString("128");
                            callback.onLinkReceived(link128); // Gọi callback với giá trị link128 khi thành công
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onLinkReceived(null); // Gọi callback với giá trị null khi có lỗi xử lý JSON
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setBackgroundBottomPlayer();
        songListLichSuBaiHat.clear();
        getSongHistory();
        new SearchTask().execute();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));
        LocalBroadcastManager.getInstance(this).registerReceiver(seekBarUpdateReceiver, new IntentFilter("send_seekbar_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(seekBarUpdateReceiver);
    }
}