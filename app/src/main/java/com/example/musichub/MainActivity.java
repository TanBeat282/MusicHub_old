package com.example.musichub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musichub.activity.BXHNewSongActivity;
import com.example.musichub.activity.HistoryActivity;
import com.example.musichub.activity.NewReleaseSongActivity;
import com.example.musichub.activity.SearchActivity;
import com.example.musichub.adapter.Playlist.PlaylistMoreAdapter;
import com.example.musichub.adapter.Album.AlbumMoreAdapter;
import com.example.musichub.adapter.radio.RadioMoreAdapter;
import com.example.musichub.adapter.Song.SongMoreAdapter;
import com.example.musichub.adapter.week_chart.WeekChartSlideAdapter;
import com.example.musichub.adapter.banner.BannerSlideAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.ChartCategories;
import com.example.musichub.api.categories.HubCategories;
import com.example.musichub.api.categories.RadioCategories;
import com.example.musichub.bottomsheet.BottomSheetProfile;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.helper.uliti.HomeDataItemTypeAdapter;
import com.example.musichub.helper.uliti.HubSectionTypeAdapter;
import com.example.musichub.model.Album.DataAlbum;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.chart.home.home_new.Home;
import com.example.musichub.model.chart.home.home_new.album.HomeDataItemPlaylistAlbum;
import com.example.musichub.model.chart.home.home_new.banner.HomeDataItemBanner;
import com.example.musichub.model.chart.home.home_new.banner.HomeDataItemBannerItem;
import com.example.musichub.model.chart.home.home_new.editor_theme.HomeDataItemPlaylistEditorTheme;
import com.example.musichub.model.chart.home.home_new.editor_theme_3.HomeDataItemPlaylistEditorTheme3;
import com.example.musichub.model.chart.home.home_new.item.HomeDataItem;
import com.example.musichub.model.chart.home.home_new.new_release.HomeDataItemNewRelease;
import com.example.musichub.model.chart.home.home_new.new_release_chart.HomeDataItemNewReleaseChart;
import com.example.musichub.model.chart.home.home_new.radio.HomeDataItemRadio;
import com.example.musichub.model.chart.home.home_new.radio.HomeDataItemRadioItem;
import com.example.musichub.model.chart.home.home_new.rt_chart.HomeDataItemRTChart;
import com.example.musichub.model.chart.home.home_new.season_theme.HomeDataItemPlaylistSeasonTheme;
import com.example.musichub.model.chart.home.home_new.top100.HomeDataItemPlaylistTop100;
import com.example.musichub.model.chart.home.home_new.week_chart.HomeDataItemWeekChart;
import com.example.musichub.model.chart.home.home_new.week_chart.HomeDataItemWeekChartItem;
import com.example.musichub.model.hub.Hub;
import com.example.musichub.model.hub.HubSection;
import com.example.musichub.model.hub.SectionHubPlaylist;
import com.example.musichub.model.hub.SectionHubSong;
import com.example.musichub.model.playlist.DataPlaylist;
import com.example.musichub.model.user_active_radio.DataUserActiveRadio;
import com.example.musichub.model.user_active_radio.UserActiveRadio;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler;
    private static final int INTERVAL = 15000; // Độ trễ giữa mỗi lần chạy, tính bằng mili giây (3 giây)
    private Home home;
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;


    //view
    private ViewPager2 view_pager_banner;
    private final Handler bannerHandler = new Handler();


    private LinearLayout btn_tat_ca, btn_viet_nam, btn_quoc_te;
    private LinearLayout linear_new_release_song, linear_bxh_new_release_song;
    private ImageView img_history, img_search, img_account;


    //init_view
    // rv_new_release_song
    private HomeDataItemNewRelease homeDataItemNewRelease;
    private RecyclerView rv_new_release_song;
    private SongMoreAdapter new_release_songAdapter;
    private ArrayList<Items> new_release_songArrayList = new ArrayList<>();


    // bxh_new_release_song
    private RecyclerView rv_bxh_new_release_song;
    private SongMoreAdapter bxh_new_release_songAdapter;
    private ArrayList<Items> bxh_new_release_songArrayList = new ArrayList<>();


    // bxh nhac
    private RecyclerView rv_bang_xep_hang;
    private SongMoreAdapter bang_xep_hangAdapter;
    private ArrayList<Items> bang_xep_hangArrayList = new ArrayList<>();


    // week chart
    private ViewPager2 view_pager_week_chart;
    private final ArrayList<HomeDataItemWeekChartItem> homeDataItemWeekChartItems = new ArrayList<>();


    //top100
    private RecyclerView rv_top100;
    private TextView txt_title_top100;
    private PlaylistMoreAdapter playlistMoreAdapter;
    private final ArrayList<DataPlaylist> dataPlaylistArrayListTop100 = new ArrayList<>();


    //playlist
    private TextView txt_title_playlist_1;
    private RecyclerView rv_playlist_1;
    private final ArrayList<DataPlaylist> dataPlaylistArrayList1 = new ArrayList<>();
    private PlaylistMoreAdapter playlistMoreAdapter1;
    private TextView txt_title_playlist_2;
    private RecyclerView rv_playlist_2;
    private final ArrayList<DataPlaylist> dataPlaylistArrayList2 = new ArrayList<>();
    private PlaylistMoreAdapter playlistMoreAdapter2;
    private TextView txt_title_playlist_3;
    private RecyclerView rv_playlist_3;
    private final ArrayList<DataPlaylist> dataPlaylistArrayList3 = new ArrayList<>();
    private PlaylistMoreAdapter playlistMoreAdapter3;

    private TextView txt_title_album;
    private RecyclerView rv_album;
    private final ArrayList<DataAlbum> dataAlbumArrayList = new ArrayList<>();
    private AlbumMoreAdapter albumMoreAdapter;

    private TextView txt_title_radio;
    private RecyclerView rv_radio;
    private final ArrayList<HomeDataItemRadioItem> homeDataItemRadioItemArrayList = new ArrayList<>();
    private RadioMoreAdapter radioMoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initViews();
        initRecyclerView();
        initAdapter();
        initBottomPlay();

        //get data
        getHome();
        getHub();
        onClick();
        mHandler = new Handler();
    }

    private void initData() {
        Helper.changeStatusBarColor(this, R.color.black);
        Helper.changeNavigationColor(this, R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        musicHelper = new MusicHelper(this, sharedPreferencesManager);
    }

    private void initViews() {
        rv_new_release_song = findViewById(R.id.rv_new_release_song);
        rv_bxh_new_release_song = findViewById(R.id.rv_bxh_new_release_song);
        rv_bang_xep_hang = findViewById(R.id.rv_bang_xep_hang);

        view_pager_week_chart = findViewById(R.id.view_pager_week_chart);
        linear_new_release_song = findViewById(R.id.linear_new_release_song);
        linear_bxh_new_release_song = findViewById(R.id.linear_bxh_new_release_song);

        img_history = findViewById(R.id.img_history);
        view_pager_banner = findViewById(R.id.view_pager_banner);

        img_search = findViewById(R.id.img_search);
        img_account = findViewById(R.id.img_account);

        //btn nhac moi phat hanh
        btn_tat_ca = findViewById(R.id.btn_tat_ca);
        btn_viet_nam = findViewById(R.id.btn_viet_nam);
        btn_quoc_te = findViewById(R.id.btn_quoc_te);

        txt_title_playlist_1 = findViewById(R.id.txt_title_playlist_1);
        rv_playlist_1 = findViewById(R.id.rv_playlist_1);
        txt_title_playlist_2 = findViewById(R.id.txt_title_playlist_2);
        rv_playlist_2 = findViewById(R.id.rv_playlist_2);
        txt_title_playlist_3 = findViewById(R.id.txt_title_playlist_3);
        rv_playlist_3 = findViewById(R.id.rv_playlist_3);
        txt_title_top100 = findViewById(R.id.txt_title_top100);
        rv_top100 = findViewById(R.id.rv_top100);

        txt_title_album = findViewById(R.id.txt_title_album);
        rv_album = findViewById(R.id.rv_album);

        txt_title_radio = findViewById(R.id.txt_title_radio);
        rv_radio = findViewById(R.id.rv_radio);
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManagerNewReleaseSong = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_new_release_song.setLayoutManager(layoutManagerNewReleaseSong);

        GridLayoutManager layoutManagerBXHNewReleaseSong = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_bxh_new_release_song.setLayoutManager(layoutManagerBXHNewReleaseSong);

        GridLayoutManager layoutManagerBangXepHang = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_bang_xep_hang.setLayoutManager(layoutManagerBangXepHang);

        LinearLayoutManager layoutManagerTop100 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_top100.setLayoutManager(layoutManagerTop100);


        LinearLayoutManager layoutManagerPlaylist1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_playlist_1.setLayoutManager(layoutManagerPlaylist1);
        LinearLayoutManager layoutManagerPlaylist2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_playlist_2.setLayoutManager(layoutManagerPlaylist2);
        LinearLayoutManager layoutManagerPlaylist3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_playlist_3.setLayoutManager(layoutManagerPlaylist3);
        LinearLayoutManager layoutManagerAlbum = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_album.setLayoutManager(layoutManagerAlbum);
        LinearLayoutManager layoutManagerRadio = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_radio.setLayoutManager(layoutManagerRadio);
    }

    private void initAdapter() {
        new_release_songAdapter = new SongMoreAdapter(new_release_songArrayList, 0, MainActivity.this, MainActivity.this);
        rv_new_release_song.setAdapter(new_release_songAdapter);

        bxh_new_release_songAdapter = new SongMoreAdapter(bxh_new_release_songArrayList, 1, MainActivity.this, MainActivity.this);
        rv_bxh_new_release_song.setAdapter(bxh_new_release_songAdapter);

        bang_xep_hangAdapter = new SongMoreAdapter(bang_xep_hangArrayList, 2, MainActivity.this, MainActivity.this);
        rv_bang_xep_hang.setAdapter(bang_xep_hangAdapter);

        playlistMoreAdapter = new PlaylistMoreAdapter(dataPlaylistArrayListTop100, MainActivity.this, MainActivity.this);
        rv_top100.setAdapter(playlistMoreAdapter);


        playlistMoreAdapter1 = new PlaylistMoreAdapter(dataPlaylistArrayList1, MainActivity.this, MainActivity.this);
        rv_playlist_1.setAdapter(playlistMoreAdapter1);
        playlistMoreAdapter2 = new PlaylistMoreAdapter(dataPlaylistArrayList2, MainActivity.this, MainActivity.this);
        rv_playlist_2.setAdapter(playlistMoreAdapter2);
        playlistMoreAdapter3 = new PlaylistMoreAdapter(dataPlaylistArrayList3, MainActivity.this, MainActivity.this);
        rv_playlist_3.setAdapter(playlistMoreAdapter3);
        albumMoreAdapter = new AlbumMoreAdapter(dataAlbumArrayList, MainActivity.this, MainActivity.this);
        rv_album.setAdapter(albumMoreAdapter);
        radioMoreAdapter = new RadioMoreAdapter(homeDataItemRadioItemArrayList, MainActivity.this, MainActivity.this);
        rv_radio.setAdapter(radioMoreAdapter);
    }

    private void initBottomPlay() {
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
        musicHelper.initAdapter(new_release_songAdapter);
        musicHelper.initAdapter(bxh_new_release_songAdapter);
        musicHelper.initAdapter(bang_xep_hangAdapter);
    }

    private void onClick() {
        img_search.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        img_history.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        img_account.setOnClickListener(view -> {
            BottomSheetProfile bottomSheetProfile = new BottomSheetProfile(MainActivity.this, MainActivity.this);
            bottomSheetProfile.show(getSupportFragmentManager(), bottomSheetProfile.getTag());
        });

        linear_new_release_song.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewReleaseSongActivity.class);
            startActivity(intent);
        });

        linear_bxh_new_release_song.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BXHNewSongActivity.class);
            startActivity(intent);
        });

        //btn nhac moi phat hanh
        btn_viet_nam.setOnClickListener(view -> checkCategoriesNewReleaseSong(1));
        btn_quoc_te.setOnClickListener(view -> checkCategoriesNewReleaseSong(2));
        btn_tat_ca.setOnClickListener(view -> checkCategoriesNewReleaseSong(0));
    }

    private void getHome() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories();
                    Map<String, String> map = chartCategories.getHome(1, 30);

                    retrofit2.Call<ResponseBody> call = service.HOME_CALL(map.get("page"), map.get("count"), map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getHome " + call.request().url());
                            if (response.isSuccessful()) {
                                try {
                                    assert response.body() != null;
                                    String jsonData = response.body().string();
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    gsonBuilder.registerTypeAdapter(HomeDataItem.class, new HomeDataItemTypeAdapter());
                                    Gson gson = gsonBuilder.create();

                                    home = gson.fromJson(jsonData, Home.class);

                                    if (home != null && home.getData() != null && home.getData().getItems() != null) {
                                        runOnUiThread(() -> {
                                            getBanner();
                                            getNewReleaseSong();
                                            getNewReleaseSongChart();
                                            getRTChart();
                                            getWeekChart();
                                            getPlaylist();
                                            getRadioLive();
                                        });
                                    }

                                } catch (Exception e) {
                                    Log.e("TAG", "Error: " + e.getMessage(), e);
                                }
                            } else {
                                Log.d("TAG", "Failed to retrieve data: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                            Log.e("TAG", "API call failed: " + throwable.getMessage(), throwable);
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

    private void getHub() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    HubCategories hubCategories = new HubCategories();
                    Map<String, String> map = hubCategories.getHub("IWZ9Z0BO");

                    retrofit2.Call<ResponseBody> call = service.HUB_DETAIL_CALL(map.get("id"), map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getHub " + call.request().url());
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String jsonData = response.body().string();
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    gsonBuilder.registerTypeAdapter(HubSection.class, new HubSectionTypeAdapter());
                                    Gson gson = gsonBuilder.create();

                                    Hub hub = gson.fromJson(jsonData, Hub.class);

                                    if (hub != null && hub.getData() != null) {
                                        ArrayList<HubSection> items = hub.getData().getSections();
                                        for (HubSection item : items) {
                                            if (item instanceof SectionHubPlaylist) {
                                                SectionHubPlaylist sectionHubSong = (SectionHubPlaylist) item;
                                                Log.d(">>>>>>>>>>", "onResponse: " + sectionHubSong.getTitle());
                                            }
                                        }
                                    } else {
                                        Log.d("TAG", "No data found in JSON");
                                    }

                                } catch (Exception e) {
                                    Log.e("TAG", "Error: " + e.getMessage(), e);
                                }
                            } else {
                                Log.d("TAG", "Response unsuccessful or empty body");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getHub1111 " + call.request().url());
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


    // banner slider
    private void getBanner() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {
                if (item instanceof HomeDataItemBanner) {
                    HomeDataItemBanner homeBanner = (HomeDataItemBanner) item;
                    setUpViewPageBanner(homeBanner);
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }

    //new release song
    @SuppressLint("NotifyDataSetChanged")
    private void checkCategoriesNewReleaseSong(int categories_nhac_moi) {
        new_release_songArrayList.clear();
        switch (categories_nhac_moi) {
            case 1:
                // viet nam
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories_check);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories);
                new_release_songArrayList.addAll(homeDataItemNewRelease.getItems().getvPop());

                break;
            case 2:
                // quoc te
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories_check);
                new_release_songArrayList.addAll(homeDataItemNewRelease.getItems().getOthers());
                break;
            default:
                // tat ca
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories_check);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories);
                new_release_songArrayList.addAll(homeDataItemNewRelease.getItems().getAll());
                break;
        }
        new_release_songAdapter.setFilterList(new_release_songArrayList);
        rv_new_release_song.scrollToPosition(0);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), new_release_songArrayList, new_release_songAdapter);
    }

    // new release song
    private void getNewReleaseSong() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {
                if (item instanceof HomeDataItemNewRelease) {
                    homeDataItemNewRelease = (HomeDataItemNewRelease) item;
                    new_release_songArrayList = homeDataItemNewRelease.getItems().getAll();
                    new_release_songAdapter.setFilterList(new_release_songArrayList);
                    musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), new_release_songArrayList, new_release_songAdapter);
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }

    // playlist
    private void getPlaylist() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {

                if (item instanceof HomeDataItemPlaylistSeasonTheme) {
                    // hSeasonTheme
                    HomeDataItemPlaylistSeasonTheme homeDataItemPlaylistSeasonTheme = (HomeDataItemPlaylistSeasonTheme) item;
                    txt_title_playlist_1.setText(homeDataItemPlaylistSeasonTheme.getTitle());
                    dataPlaylistArrayList1.addAll(homeDataItemPlaylistSeasonTheme.getItems());
                    playlistMoreAdapter1.setFilterList(homeDataItemPlaylistSeasonTheme.getItems());
                } else if (item instanceof HomeDataItemPlaylistEditorTheme) {
                    // hEditorTheme
                    HomeDataItemPlaylistEditorTheme homeDataItemPlaylistEditorTheme = (HomeDataItemPlaylistEditorTheme) item;
                    txt_title_playlist_2.setText(homeDataItemPlaylistEditorTheme.getTitle());
                    dataPlaylistArrayList2.addAll(homeDataItemPlaylistEditorTheme.getItems());
                    playlistMoreAdapter2.setFilterList(homeDataItemPlaylistEditorTheme.getItems());
                } else if (item instanceof HomeDataItemPlaylistEditorTheme3) {
                    // hEditorTheme3
                    HomeDataItemPlaylistEditorTheme3 homeDataItemPlaylistEditorTheme3 = (HomeDataItemPlaylistEditorTheme3) item;
                    txt_title_playlist_3.setText(homeDataItemPlaylistEditorTheme3.getTitle());
                    dataPlaylistArrayList3.addAll(homeDataItemPlaylistEditorTheme3.getItems());
                    playlistMoreAdapter3.setFilterList(homeDataItemPlaylistEditorTheme3.getItems());
                } else if (item instanceof HomeDataItemPlaylistTop100) {
                    // hTop100
                    HomeDataItemPlaylistTop100 homeDataItemPlaylistTop100 = (HomeDataItemPlaylistTop100) item;
                    txt_title_top100.setText(homeDataItemPlaylistTop100.getTitle());
                    dataPlaylistArrayListTop100.addAll(homeDataItemPlaylistTop100.getItems());
                    playlistMoreAdapter.setFilterList(homeDataItemPlaylistTop100.getItems());
                } else if (item instanceof HomeDataItemPlaylistAlbum) {
                    // hAlbum
                    HomeDataItemPlaylistAlbum homeDataItemPLaylistAlbum = (HomeDataItemPlaylistAlbum) item;
                    txt_title_album.setText(homeDataItemPLaylistAlbum.getTitle());
                    dataAlbumArrayList.addAll(homeDataItemPLaylistAlbum.getItems());
                    albumMoreAdapter.setFilterList(homeDataItemPLaylistAlbum.getItems());
                } else {
                    Log.d("TAG", "Unknown HomeDataItem type: " + item.getClass().getSimpleName());
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }


    // bxh new release song
    private void getNewReleaseSongChart() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {
                if (item instanceof HomeDataItemNewReleaseChart) {
                    HomeDataItemNewReleaseChart homeDataItemNewReleaseChart = (HomeDataItemNewReleaseChart) item;
                    bxh_new_release_songArrayList = homeDataItemNewReleaseChart.getItems();
                    bxh_new_release_songAdapter.setFilterList(homeDataItemNewReleaseChart.getItems());
                    musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bxh_new_release_songArrayList, bxh_new_release_songAdapter);
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }


    // rt chart
    private void getRTChart() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {
                if (item instanceof HomeDataItemRTChart) {
                    HomeDataItemRTChart homeDataItemRTChart = (HomeDataItemRTChart) item;
                    bang_xep_hangArrayList = homeDataItemRTChart.getItems();
                    bang_xep_hangAdapter.setFilterList(homeDataItemRTChart.getItems());
                    musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bang_xep_hangArrayList, bang_xep_hangAdapter);
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }


    // week chart
    private void getWeekChart() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {
                if (item instanceof HomeDataItemWeekChart) {
                    HomeDataItemWeekChart homeDataItemWeekChart = (HomeDataItemWeekChart) item;
                    setUpViewPageWeekChart(homeDataItemWeekChart);
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }

    // new release song
    private void getRadioLive() {
        if (home != null && home.getData() != null && home.getData().getItems() != null) {
            ArrayList<HomeDataItem> items = home.getData().getItems();
            for (HomeDataItem item : items) {
                if (item instanceof HomeDataItemRadio) {
                    HomeDataItemRadio homeDataItemRadio = (HomeDataItemRadio) item;
                    txt_title_radio.setText(homeDataItemRadio.getTitle());
                    homeDataItemRadioItemArrayList.addAll(homeDataItemRadio.getItems());
                    radioMoreAdapter.setFilterList(homeDataItemRadio.getItems());
                    // Sử dụng mHandler để lập lịch gọi hàm getUserActiveRadio(homeDataItemRadioItemArrayList)
                    startRepeatingTask();
                }
            }
        } else {
            Log.d("TAG", "No data found in JSON");
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                // Gọi hàm getUserActiveRadio(homeDataItemRadioItemArrayList)
                getUserActiveRadio(homeDataItemRadioItemArrayList);
            } finally {
                // Lập lịch gọi lại chính nó sau 3 giây
                mHandler.postDelayed(mStatusChecker, INTERVAL);
            }
        }
    };

    private void startRepeatingTask() {
        // Bắt đầu lập lịch gọi lại phương thức mStatusChecker
        mHandler.postDelayed(mStatusChecker, INTERVAL);
    }

    private void setUpViewPageWeekChart(HomeDataItemWeekChart homeDataItemWeekChart) {
        homeDataItemWeekChartItems.add(homeDataItemWeekChart.getItems().get(1));
        homeDataItemWeekChartItems.add(homeDataItemWeekChart.getItems().get(0));
        homeDataItemWeekChartItems.add(homeDataItemWeekChart.getItems().get(2));

        view_pager_week_chart.setAdapter(new WeekChartSlideAdapter(homeDataItemWeekChartItems, view_pager_week_chart, MainActivity.this, MainActivity.this));
        view_pager_week_chart.setClipToPadding(false);
        view_pager_week_chart.setClipChildren(false);
        view_pager_week_chart.setOffscreenPageLimit(3);
        view_pager_week_chart.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        view_pager_week_chart.setPageTransformer(compositePageTransformer);
        view_pager_week_chart.setCurrentItem(1, false);
    }

    private void setUpViewPageBanner(HomeDataItemBanner homeDataItemBanner) {
        ArrayList<HomeDataItemBannerItem> bannerItems = homeDataItemBanner.getItems();
        BannerSlideAdapter adapter = new BannerSlideAdapter(bannerItems, view_pager_banner, this, this);

        view_pager_banner.setAdapter(adapter);
        view_pager_banner.setClipToPadding(false);
        view_pager_banner.setClipChildren(false);
        view_pager_banner.setOffscreenPageLimit(3);
        view_pager_banner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float scaleFactor = 0.90f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scaleFactor);
        });
        view_pager_banner.setPageTransformer(transformer);

        view_pager_banner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 3000);
            }
        });
    }

    private final Runnable bannerRunnable = () -> view_pager_banner.setCurrentItem(view_pager_banner.getCurrentItem() + 1);

    private void getUserActiveRadio(ArrayList<HomeDataItemRadioItem> homeDataItemRadioItemArrayList) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    RadioCategories radioCategories = new RadioCategories();
                    StringBuilder idsBuilder = new StringBuilder();
                    for (int i = 0; i < homeDataItemRadioItemArrayList.size(); i++) {
                        HomeDataItemRadioItem item = homeDataItemRadioItemArrayList.get(i);
                        idsBuilder.append(item.getEncodeId());
                        if (i < homeDataItemRadioItemArrayList.size() - 1) {
                            idsBuilder.append(",");
                        }
                    }
                    String ids = idsBuilder.toString();

                    Map<String, String> map = radioCategories.getUserActiveRadio(ids);
                    retrofit2.Call<UserActiveRadio> call = service.USER_ACTIVE_RADIO_CALL(ids, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));

                    call.enqueue(new Callback<UserActiveRadio>() {
                        @Override
                        public void onResponse(Call<UserActiveRadio> call, Response<UserActiveRadio> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getUserActiveRadio " + call.request().url());
                            if (response.isSuccessful()) {
                                UserActiveRadio userActiveRadio = response.body();
                                if (userActiveRadio != null) {
                                    for (DataUserActiveRadio dataUserActiveRadio : userActiveRadio.getData()) {
                                        for (HomeDataItemRadioItem homeDataItemRadioItem : homeDataItemRadioItemArrayList) {
                                            if (homeDataItemRadioItem.getEncodeId().equals(dataUserActiveRadio.getEncodeId())) {
                                                homeDataItemRadioItem.setActiveUsers(dataUserActiveRadio.getActiveUsers());
                                            }
                                        }
                                    }
                                    radioMoreAdapter.setFilterList(homeDataItemRadioItemArrayList);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserActiveRadio> call, Throwable throwable) {
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
        bannerHandler.postDelayed(bannerRunnable, 3000);
        startRepeatingTask();

        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), new_release_songArrayList, new_release_songAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bxh_new_release_songArrayList, bxh_new_release_songAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bang_xep_hangArrayList, bang_xep_hangAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
        mHandler.removeCallbacks(mStatusChecker);
    }
}