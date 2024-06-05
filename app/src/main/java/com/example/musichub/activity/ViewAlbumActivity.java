package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.example.musichub.adapter.Artist.SelectArtistAdapter;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.BlurAndBlackOverlayTransformation;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.playlist.Playlist;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAlbumActivity extends AppCompatActivity {
    private Playlist playlist;
    private NestedScrollView nested_scroll_view;
    private RelativeLayout relative_header;
    private ImageView img_back;
    private TextView txt_name_artist;
    private TextView txt_view;
    private TextView txt_content_playlist;
    private ImageView imageBackground;
    private RoundedImageView img_playlist;
    private ProgressBar progress_image;
    private TextView txt_title_playlist;
    private TextView txt_user_name;
    private TextView txt_song_and_time;

    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private SongAllAdapter songAllAdapter;
    private RecyclerView rv_album;

    private TextView txt_releaseDate, txt_count_song, txt_time_song, txt_distributor;

    private RecyclerView rv_artist;
    private SelectArtistAdapter selectArtistAdapter;
    private ArrayList<Artists> artistsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_view_album);

        Helper.changeNavigationColor(this, R.color.gray, true);


        initView();
        configView();
        onClick();
        initAdapter();
        getDataBundle();
    }

    private void initView() {
        imageBackground = findViewById(R.id.imageBackground);
        relative_header = findViewById(R.id.relative_header);
        img_back = findViewById(R.id.img_back);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);
        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_content_playlist = findViewById(R.id.txt_content_playlist);
        txt_content_playlist.setSelected(true);
        txt_view = findViewById(R.id.txt_view);

        img_playlist = findViewById(R.id.img_playlist);
        progress_image = findViewById(R.id.progress_image);
        txt_title_playlist = findViewById(R.id.txt_title_playlist);
        txt_title_playlist.setSelected(true);
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_song_and_time = findViewById(R.id.txt_song_and_time);

        rv_album = findViewById(R.id.rv_album);

        txt_releaseDate = findViewById(R.id.txt_releaseDate);
        txt_count_song = findViewById(R.id.txt_count_song);
        txt_time_song = findViewById(R.id.txt_time_song);
        txt_distributor = findViewById(R.id.txt_distributor);

        rv_artist = findViewById(R.id.rv_artist);
    }

    private void configView() {
        nested_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
                    txt_name_artist.setText(playlist.getData().getTitle());
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewAlbumActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewAlbumActivity.this, R.color.gray);
                }
            }
        });
    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_album.setLayoutManager(layoutManager);
        songAllAdapter = new SongAllAdapter(itemsArrayList, ViewAlbumActivity.this, ViewAlbumActivity.this);
        rv_album.setAdapter(songAllAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAlbumActivity.this, LinearLayoutManager.VERTICAL, false);
        rv_artist.setLayoutManager(linearLayoutManager);
        selectArtistAdapter = new SelectArtistAdapter(artistsArrayList, ViewAlbumActivity.this, ViewAlbumActivity.this);
        rv_artist.setAdapter(selectArtistAdapter);
    }

    private void onClick() {
        img_back.setOnClickListener(view -> finish());
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String encodeId = bundle.getString("album_endCodeId");
            getAlbum(encodeId);
        }
    }

    private void getAlbum(String encodeId) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getPlaylist(encodeId);

                    retrofit2.Call<Playlist> call = service.PLAYLIST_CALL(encodeId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Playlist>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NonNull Call<Playlist> call, @NonNull Response<Playlist> response) {
                            String requestUrl = call.request().url().toString();
                            Log.d(">>>>>>>>>>>>>>>>>>>", " - " + requestUrl);
                            if (response.isSuccessful()) {
                                playlist = response.body();
                                if (playlist != null && playlist.getErr() == 0) {
                                    ArrayList<Items> arrayList = playlist.getData().getSong().getItems();

                                    if (!arrayList.isEmpty()) {
                                        runOnUiThread(() -> {

                                            // Sử dụng Glide để tải và áp dụng hiệu ứng mờ
                                            Glide.with(ViewAlbumActivity.this)
                                                    .load(playlist.getData().getThumbnailM())
                                                    .transform(new CenterCrop(), new BlurAndBlackOverlayTransformation(ViewAlbumActivity.this, 25, 220)) // 25 là mức độ mờ, 150 là độ mờ của lớp phủ đen
                                                    .into(imageBackground);

                                            Glide.with(ViewAlbumActivity.this)
                                                    .load(playlist.getData().getThumbnailM())
                                                    .into(img_playlist);

                                            img_playlist.setVisibility(View.VISIBLE);
                                            progress_image.setVisibility(View.GONE);

                                            txt_title_playlist.setText(playlist.getData().getTitle());
                                            txt_user_name.setText(playlist.getData().getArtistsNames());

                                            txt_song_and_time.setText("Album · 2024");
                                            txt_content_playlist.setText(playlist.getData().getDescription());

                                            itemsArrayList = arrayList;
                                            songAllAdapter.setFilterList(arrayList);

                                            txt_releaseDate.setText(playlist.getData().getReleaseDate());
                                            txt_count_song.setText(arrayList.size() + " bài hát, ");
                                            txt_time_song.setText(convertLongToString(playlist.getData().getSong().getTotalDuration()));
                                            txt_distributor.setText(playlist.getData().getDistributor());
                                            artistsArrayList = playlist.getData().getArtists();

                                            selectArtistAdapter.setFilterList(playlist.getData().getArtists());

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

    private String convertLongToString(long time) {
        int gio = (int) (time / 3600);
        int phut = (int) ((time % 3600) / 60);

        return gio == 0 ? phut + " phút" : gio + " giờ " + phut + " phút";
    }
}