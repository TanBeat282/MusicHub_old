package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.musichub.R;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.BlurAndBlackOverlayTransformation;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Album;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.playlist.DataPlaylist;
import com.example.musichub.model.playlist.Playlist;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlaylistActivity extends AppCompatActivity {
    private ImageView imageBackground;
    private RoundedImageView img_playlist;
    private ProgressBar progress_image;
    private TextView txt_title_playlist;
    private TextView txt_user_name;
    private TextView txt_song_and_time;
    private RelativeLayout relative_header;
    private TextView txt_name_artist;
    private TextView txt_view;
    private TextView txt_content_playlist;
    private DataPlaylist dataPlaylist;
    private Album album;
    private ArrayList<Items> itemsArrayList;
    private SongAllAdapter songAllAdapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        setContentView(R.layout.activity_view_playlist);
        Helper.changeNavigationColor(this, R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        musicHelper = new MusicHelper(this, sharedPreferencesManager);


        imageBackground = findViewById(R.id.imageBackground);
        ImageView img_back = findViewById(R.id.img_back);
        ImageView img_more = findViewById(R.id.img_more);

        relative_header = findViewById(R.id.relative_header);
        NestedScrollView nested_scroll = findViewById(R.id.nested_scroll_view);
        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_view = findViewById(R.id.txt_view);

        img_playlist = findViewById(R.id.img_playlist);
        progress_image = findViewById(R.id.progress_image);
        txt_title_playlist = findViewById(R.id.txt_title_playlist);
        txt_title_playlist.setSelected(true);
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_song_and_time = findViewById(R.id.txt_song_and_time);
        LinearLayout btn_play_playlist = findViewById(R.id.btn_play_playlist);
        txt_content_playlist = findViewById(R.id.txt_content_playlist);
        RecyclerView rv_playlist = findViewById(R.id.rv_playlist);

        itemsArrayList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_playlist.setLayoutManager(layoutManager);
        songAllAdapter = new SongAllAdapter(itemsArrayList, ViewPlaylistActivity.this, ViewPlaylistActivity.this);
        rv_playlist.setAdapter(songAllAdapter);


        // Khởi tạo các view
        View layoutPlayerBottom = findViewById(R.id.layoutPlayerBottom);
        LinearLayout layoutPlayer = layoutPlayerBottom.findViewById(R.id.layoutPlayer);
        LinearLayout linearPlayPause = layoutPlayerBottom.findViewById(R.id.linear_play_pause);
        ImageView imgPlayPause = layoutPlayerBottom.findViewById(R.id.img_play_pause);
        LinearLayout linearNext = layoutPlayerBottom.findViewById(R.id.linear_next);
        ImageView imgAlbumSong = layoutPlayerBottom.findViewById(R.id.img_album_song);
        TextView tvTitleSong = layoutPlayerBottom.findViewById(R.id.txtTile);
        tvTitleSong.setSelected(true);
        TextView tvSingleSong = layoutPlayerBottom.findViewById(R.id.txtArtist);
        tvSingleSong.setSelected(true);
        LinearProgressIndicator progressIndicator = layoutPlayerBottom.findViewById(R.id.progressIndicator);

        musicHelper.initViews(layoutPlayerBottom, layoutPlayer, linearPlayPause, imgPlayPause, linearNext, imgAlbumSong, tvTitleSong, tvSingleSong, progressIndicator);

        // Lấy thông tin bài hát hiện tại
        musicHelper.getSongCurrent();
        musicHelper.initAdapter(songAllAdapter);

        img_back.setOnClickListener(view -> finish());

        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Kiểm tra nếu người dùng đã cuộn đến đầu trang
                if (scrollY <= 600) {
                    // Ẩn TextView khi người dùng cuộn trở lại đầu trang
                    txt_name_artist.setVisibility(View.GONE);
                    txt_view.setVisibility(View.VISIBLE);
                    relative_header.setBackgroundResource(android.R.color.transparent);
                    // Make the content appear under the status bar
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
                    }

                } else if (scrollY >= 800) {
                    // Hiển thị TextView khi người dùng cuộn xuống khỏi đầu trang
                    txt_name_artist.setVisibility(View.VISIBLE);
                    txt_view.setVisibility(View.GONE);
                    txt_name_artist.setText(dataPlaylist == null ? album.getTitle() : dataPlaylist.getTitle());
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewPlaylistActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewPlaylistActivity.this, R.color.gray);
                }
            }
        });

        getDataBundle();
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
        } else {
            if (bundle.getSerializable("playlist") instanceof DataPlaylist) {
                dataPlaylist = (DataPlaylist) bundle.getSerializable("playlist");
                getPlaylist(dataPlaylist.getEncodeId());
                // Sử dụng Glide để tải và áp dụng hiệu ứng mờ
                Glide.with(this)
                        .load(dataPlaylist.getThumbnailM())
                        .transform(new CenterCrop(), new BlurAndBlackOverlayTransformation(this, 25, 220)) // 25 là mức độ mờ, 150 là độ mờ của lớp phủ đen
                        .into(imageBackground);
            } else {
                album = (Album) bundle.getSerializable("playlist");
                getPlaylist(album.getEncodeId());
                // Sử dụng Glide để tải và áp dụng hiệu ứng mờ
                Glide.with(this)
                        .load(album.getThumbnail())
                        .transform(new CenterCrop(), new BlurAndBlackOverlayTransformation(this, 25, 220)) // 25 là mức độ mờ, 150 là độ mờ của lớp phủ đen
                        .into(imageBackground);

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
                        public void onResponse(@NonNull Call<Playlist> call, @NonNull Response<Playlist> response) {
                            String requestUrl = call.request().url().toString();
                            Log.d(">>>>>>>>>>>>>>>>>>>", " - " + requestUrl);
                            if (response.isSuccessful()) {
                                Playlist playlist = response.body();
                                if (playlist != null && playlist.getErr() == 0) {
                                    ArrayList<Items> arrayList = playlist.getData().getSong().getItems();
                                    if (!arrayList.isEmpty()) {
                                        runOnUiThread(() -> {

                                            Glide.with(ViewPlaylistActivity.this)
                                                    .load(playlist.getData().getThumbnailM())
                                                    .into(img_playlist);

                                            img_playlist.setVisibility(View.VISIBLE);
                                            progress_image.setVisibility(View.GONE);

                                            txt_title_playlist.setText(playlist.getData().getTitle());
                                            txt_user_name.setText(playlist.getData().getUserName());

                                            txt_song_and_time.setText(convertLongToString(arrayList.size(), playlist.getData().getSong().getTotalDuration()));
                                            txt_content_playlist.setText(playlist.getData().getDescription());

                                            itemsArrayList = arrayList;
                                            songAllAdapter.setFilterList(arrayList);
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
                        public void onFailure(@NonNull Call<Playlist> call, @NonNull Throwable throwable) {
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

    private String convertLongToString(int size, long time) {
        int gio = (int) (time / 3600);
        int phut = (int) ((time % 3600) / 60);

        return size + " bài hát · " + gio + " giờ " + phut + " phút";
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicHelper.registerReceivers();
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }

}