package com.example.musichub.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.chart.top100.ItemsTop100;
import com.example.musichub.model.playlist.Playlist;
import com.example.musichub.service.MyService;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlaylistActivity extends AppCompatActivity {
    private ImageView imageBackground;
    private ImageView btn_back;
    private RoundedImageView img_playlist;
    private TextView txt_title_playlist;
    private TextView txt_user_name;
    private TextView txt_song_and_time;
    private LinearLayout btn_play_playlist;
    private TextView txt_content_playlist;
    private RecyclerView rv_playlist;
    private ItemsTop100 itemsTop100;
    private Items items, mSong;
    private boolean isPlaying;
    private int action;
    private ArrayList<Items> itemsArrayList;
    private TopSongAdapter topSongAdapter;
    private SharedPreferencesManager sharedPreferencesManager;

    private View layoutPlayerBottom;
    private LinearLayout layoutPlayer, linear_play_pause, linear_next;
    private ImageView img_play_pause;
    private RoundedImageView img_album_song;
    private TextView tvTitleSong, tvSingleSong;
    private LinearProgressIndicator progressIndicator;
    private int currentTime, total_time;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            mSong = (Items) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            action = bundle.getInt("action_music");
            handleLayoutMusic(action);
            checkIsPlayingPlaylist(mSong, itemsArrayList);
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
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_playlist);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        items = sharedPreferencesManager.restoreSongState();

        imageBackground = findViewById(R.id.imageBackground);
        btn_back = findViewById(R.id.btn_back);
        img_playlist = findViewById(R.id.img_playlist);
        txt_title_playlist = findViewById(R.id.txt_title_playlist);
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_song_and_time = findViewById(R.id.txt_song_and_time);
        btn_play_playlist = findViewById(R.id.btn_play_playlist);
        txt_content_playlist = findViewById(R.id.txt_content_playlist);
        rv_playlist = findViewById(R.id.rv_playlist);

        layoutPlayerBottom = findViewById(R.id.layoutPlayerBottom);
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

        itemsArrayList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_playlist.setLayoutManager(layoutManager);
        topSongAdapter = new TopSongAdapter(itemsArrayList, ViewPlaylistActivity.this,ViewPlaylistActivity.this);
        rv_playlist.setAdapter(topSongAdapter);

        layoutPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(ViewPlaylistActivity.this, PlayNowActivity.class);
            startActivity(intent);
        });

        getDataBundle();
        getSongCurrent();
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        } else {
            itemsTop100 = (ItemsTop100) bundle.getSerializable("song");
            getPlaylist(itemsTop100.getEncodeId());
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }

    private void showInfoSong() {
        if (mSong == null) {
            return;
        }

        Glide.with(this)
                .load(mSong.getThumbnail())
                .into(img_album_song);
        tvTitleSong.setText(mSong.getTitle());
        tvSingleSong.setText(mSong.getArtistsNames());

        linear_play_pause.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(ViewPlaylistActivity.this, MyService.class)) {
                startService(new Intent(this, MyService.class));
            }
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
            }
        });
        linear_next.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(ViewPlaylistActivity.this, MyService.class)) {
                startService(new Intent(ViewPlaylistActivity.this, MyService.class));
            }
            sendActionToService(MyService.ACTION_NEXT);
        });
        int color = getResources().getColor(R.color.gray);
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        ViewCompat.setBackgroundTintList(layoutPlayer, colorStateList);

    }

    private void setStatusButtonPlayOrPause() {
        if (!Helper.isMyServiceRunning(ViewPlaylistActivity.this, MyService.class)) {
            isPlaying = false;
        }
        if (isPlaying) {
            img_play_pause.setImageResource(R.drawable.baseline_pause_24);
        } else {
            img_play_pause.setImageResource(R.drawable.baseline_play_arrow_24);

        }
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

    private void getSongCurrent() {
        mSong = sharedPreferencesManager.restoreSongState();
        isPlaying = sharedPreferencesManager.restoreIsPlayState();
        action = sharedPreferencesManager.restoreActionState();
//        getBaiHatNhanh(mSong);
//        new SearchTask().execute();
        handleLayoutMusic(action);
    }

    private void updateIndicator(int currentTime, int totalTime) {
        if (totalTime > 0) {
            float progress = (float) currentTime / totalTime;
            int progressInt = (int) (progress * 100);
            progressIndicator.setProgressCompat(progressInt, true);
        }
    }

    private void checkIsPlayingTop(Items items, ArrayList<Items> songList) {
        if (items == null || songList == null) {
            return;
        }

        String currentEncodeId = items.getEncodeId();
        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
            for (Items song : songList) {
                if (currentEncodeId.equals(song.getEncodeId())) {
                    topSongAdapter.updatePlayingStatus(currentEncodeId);
                    break;
                }
            }
        }
    }

    private void getPlaylist(String encodeId) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getPlaylist(encodeId);

                    retrofit2.Call<Playlist> call = service.PLAYLIST_CALL(encodeId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Playlist>() {
                        @Override
                        public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                            if (response.isSuccessful()) {
                                Playlist playlist = response.body();
                                if (playlist != null && playlist.getErr() == 0) {
                                    ArrayList<Items> arrayList = playlist.getData().getSong().getItems();
                                    if (!arrayList.isEmpty()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

//                                                imageBackground.setBackgroundColor(getColor(playlist.getData().getThumbnail()));

                                                Glide.with(ViewPlaylistActivity.this)
                                                        .load(playlist.getData().getThumbnailM())
                                                        .into(img_playlist);

                                                txt_title_playlist.setText(playlist.getData().getTitle());
                                                txt_user_name.setText(playlist.getData().getUserName());

                                                txt_song_and_time.setText(convertLongToString(arrayList.size(), playlist.getData().getSong().getTotalDuration()));
                                                txt_content_playlist.setText(playlist.getData().getDescription());

                                                itemsArrayList = arrayList;
                                                topSongAdapter.setFilterList(arrayList);
                                                checkIsPlayingTop(items, arrayList);
                                            }
                                        });
                                    } else {
                                        Log.d("TAG", "Items list is empty");
                                    }
                                } else {
                                    Log.d("TAG", "Error: ");
                                }
                            } else {
                                Log.d("TAG", "Failed to retrieve data: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Playlist> call, Throwable throwable) {
                            Log.d("TAG", "Failed to retrieve data: " + throwable);
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private int getColor(String urlImage) {
        final int[] brighterColor = new int[1]; // Sử dụng mảng để lưu giá trị mới

        Glide.with(this)
                .asBitmap()
                .load(urlImage)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource).generate(palette -> {
                            int dominantColor = palette.getDominantColor(getResources().getColor(R.color.default_color));
                            float[] hsv = new float[3];
                            Color.colorToHSV(dominantColor, hsv);
                            hsv[2] *= 1.2f;
                            brighterColor[0] = Color.HSVToColor(hsv); // Lưu giá trị vào mảng
                        });
                        return false;
                    }
                })
                .submit();

        return brighterColor[0]; // Trả về giá trị mới
    }
    private void checkIsPlayingPlaylist(Items items, ArrayList<Items> songList) {
        if (items == null || songList == null) {
            return;
        }

        String currentEncodeId = items.getEncodeId();
        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
            for (Items song : songList) {
                if (currentEncodeId.equals(song.getEncodeId())) {
                    topSongAdapter.updatePlayingStatus(currentEncodeId);
                    break;
                }
            }
        }
    }


    private String convertLongToString(int size, long time) {
        int gio = (int) (time / 3600);
        int phut = (int) ((time % 3600) / 60);
        int giay = (int) (time % 60);

        return size + " bài hát · " + gio + " giờ " + phut + " phút";
    }

    @Override
    protected void onResume() {
        super.onResume();
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