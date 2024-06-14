package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichub.R;
import com.example.musichub.adapter.Song.PaginationScrollListener;
import com.example.musichub.adapter.Song.SongAllMoreAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.ArtistCategories;
import com.example.musichub.bottomsheet.BottomSheetOptionSong;
import com.example.musichub.bottomsheet.BottomSheetSelectSortSong;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.song_of_artist.SongOfArtist;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllSongActivity extends AppCompatActivity implements BottomSheetSelectSortSong.SortOptionListener {
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;
    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private SongOfArtist songOfArtist;
    private SongAllMoreAdapter songAllMoreAdapter;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    private int totalPages;
    private int sort = 2;
    private String sortString = "listen";

    private String id;
    private String sectionId;

    ///////

    private RelativeLayout nested_scroll;
    private RelativeLayout relative_header;
    private ImageView img_back, img_more;
    private TextView txt_name_artist, txt_view;
    private TextView txt_song_of_artist;
    private LinearLayout linear_filter_song;
    private LinearLayout linear_play_song;
    private TextView txt_filter_song;
    private ImageView img_filter_song;
    private RecyclerView rv_song_of_artist;
    private View layoutPlayerBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Helper.changeStatusBarColor(ViewAllSongActivity.this, R.color.black);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        musicHelper = new MusicHelper(this, sharedPreferencesManager);

        initView();
        configView();
        initBottomPlayer();
        initAdapter();
        onClick();
        getBundleSong();

    }

    private void initView() {
        nested_scroll = findViewById(R.id.nested_scroll);

        relative_header = findViewById(R.id.relative_header);
        img_back = findViewById(R.id.img_back);
        img_more = findViewById(R.id.img_more);

        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_view = findViewById(R.id.txt_view);
        txt_song_of_artist = findViewById(R.id.txt_song_of_artist);

        linear_filter_song = findViewById(R.id.linear_filter_song);
        linear_play_song = findViewById(R.id.linear_play_song);
        txt_filter_song = findViewById(R.id.txt_filter_song);
        img_filter_song = findViewById(R.id.img_filter_song);

        rv_song_of_artist = findViewById(R.id.rv_song_of_artist);

        layoutPlayerBottom = findViewById(R.id.layoutPlayerBottom);
    }

    private void configView() {

//        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
//            @Override
//            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY <= 200) {
//                    txt_name_artist.setVisibility(View.GONE);
//                    txt_view.setVisibility(View.VISIBLE);
//                    relative_header.setBackgroundResource(android.R.color.transparent);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//                        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
//                    }
//
//                } else if (scrollY >= 300) {
//                    txt_name_artist.setVisibility(View.VISIBLE);
//                    txt_view.setVisibility(View.GONE);
//                    txt_name_artist.setText(songOfArtist.getMsg());
//                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewAllSongActivity.this, R.color.gray));
//                    Helper.changeStatusBarColor(ViewAllSongActivity.this, R.color.gray);
//                }
//            }
//        });
        txt_filter_song.setText(sort == 1 ? "Nghe nhiều" : "Mới nhất");
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_song_of_artist.setLayoutManager(linearLayoutManager);

        songAllMoreAdapter = new SongAllMoreAdapter(itemsArrayList, ViewAllSongActivity.this, ViewAllSongActivity.this);
        rv_song_of_artist.setAdapter(songAllMoreAdapter);

        rv_song_of_artist.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                new Handler().postDelayed(() -> loadNextPage(id, currentPage, sortString, sectionId), 300);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });
    }

    private void onClick() {
        img_back.setOnClickListener(view1 -> finish());
        linear_play_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemsArrayList.get(0).getStreamingStatus() == 2) {
                    Toast.makeText(ViewAllSongActivity.this, "Không thể phát bài hát Premium!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ViewAllSongActivity.this, PlayNowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("song", itemsArrayList.get(0));
                    bundle.putInt("position_song", 0);
                    bundle.putSerializable("song_list", itemsArrayList);
                    bundle.putInt("title_now_playing", 0);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            }
        });
        linear_filter_song.setOnClickListener(view -> {
            BottomSheetSelectSortSong bottomSheetSelectSortSong = new BottomSheetSelectSortSong(ViewAllSongActivity.this, ViewAllSongActivity.this, sort);
            bottomSheetSelectSortSong.show(getSupportFragmentManager(), bottomSheetSelectSortSong.getTag());
        });
    }

    private void initBottomPlayer() {
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
        musicHelper.initAdapter(songAllMoreAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllMoreAdapter);
    }

    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
            sectionId = bundle.getString("sectionId");
            if (id != null && sectionId != null) {
                getSongListOfArtist(id, currentPage, "listen", sectionId);
            }
        }

    }

    public int calculateTotalPages(int totalItems) {
        return (int) Math.ceil((double) totalItems / 30);
    }

    private void getSongListOfArtist(String id, int page, String sort, String sectionId) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ArtistCategories artistCategories = new ArtistCategories();
                    Map<String, String> map = artistCategories.getSongListOfArtist(id, String.valueOf(page));
                    Call<SongOfArtist> call = service.SONG_LIST_OF_ARTIST_CALL(id, "artist", String.valueOf(page), "30", sort, sectionId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<SongOfArtist>() {
                        @Override
                        public void onResponse(Call<SongOfArtist> call, Response<SongOfArtist> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>>", "getSongListOfArtist " + call.request().url());
                            if (response.isSuccessful()) {
                                songOfArtist = response.body();
                                if (songOfArtist != null && songOfArtist.getErr() == 0) {
                                    ArrayList<Items> arrayList = songOfArtist.getData().getItems();
                                    if (!arrayList.isEmpty()) {
                                        runOnUiThread(() -> {
                                            itemsArrayList = arrayList;
                                            songAllMoreAdapter.setFilterList(arrayList);
                                            totalPages = calculateTotalPages(songOfArtist.getData().getTotal());

                                            if (currentPage < totalPages) {
                                                songAllMoreAdapter.addFooterLoading();
                                            } else {
                                                isLastPage = true;
                                            }
                                            rv_song_of_artist.scrollToPosition(0);
                                            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllMoreAdapter);
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
                        public void onFailure(Call<SongOfArtist> call, Throwable throwable) {

                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG", "Service creation error: " + e.getMessage(), e);
            }
        });
    }

    private void loadNextPage(String id, int page, String sort, String sectionId) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ArtistCategories artistCategories = new ArtistCategories();
                    Map<String, String> map = artistCategories.getSongListOfArtist(id, String.valueOf(page));
                    Call<SongOfArtist> call = service.SONG_LIST_OF_ARTIST_CALL(id, "artist", String.valueOf(page), "30", sort, sectionId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<SongOfArtist>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(Call<SongOfArtist> call, Response<SongOfArtist> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>>", "loadNextPage " + currentPage + " " + call.request().url());
                            if (response.isSuccessful()) {
                                songOfArtist = response.body();
                                if (songOfArtist != null && songOfArtist.getErr() == 0) {
                                    ArrayList<Items> arrayList = songOfArtist.getData().getItems();
                                    if (!arrayList.isEmpty()) {
                                        runOnUiThread(() -> {
                                            songAllMoreAdapter.removeFooterLoading();
                                            itemsArrayList.addAll(arrayList);
                                            songAllMoreAdapter.notifyDataSetChanged();
                                            isLoading = false;
                                            if (currentPage < totalPages) {
                                                songAllMoreAdapter.addFooterLoading();
                                            } else {
                                                isLastPage = true;
                                            }
                                            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllMoreAdapter);
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
                        public void onFailure(Call<SongOfArtist> call, Throwable throwable) {

                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG", "Service creation error: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicHelper.registerReceivers();
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllMoreAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }

    @Override
    public void onSortOptionSelected(int sortOption) {
        sort = sortOption;
        sortString = sort == 1 ? "listen" : "new";
        txt_filter_song.setText(sort == 1 ? "Nghe nhiều" : "Mới nhất");
        getSongListOfArtist(id, currentPage, sortString, sectionId);
    }
}