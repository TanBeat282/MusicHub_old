package com.example.musichub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.musichub.activity.BXHNewSongActivity;
import com.example.musichub.activity.HistoryActivity;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.activity.SearchActivity;
import com.example.musichub.adapter.SongAdapter.SongMoreAdapter;
import com.example.musichub.adapter.Top100Adapter.Top100MoreAdapter;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.ChartCategories;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.bottomsheet.BottomSheetProfile;
import com.example.musichub.bottomsheet.BottomSheetSelectArtist;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.ChartHome;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.chart.home.DataHomeAll;
import com.example.musichub.model.chart.home.DataHomeSlider;
import com.example.musichub.model.chart.home.ItemSlider;
import com.example.musichub.model.chart.home.ItemsData;
import com.example.musichub.model.chart.new_release.NewRelease;
import com.example.musichub.model.chart.top100.Top100;
import com.example.musichub.model.playlist.DataPlaylist;
import com.example.musichub.model.playlist.Playlist;
import com.example.musichub.service.MyService;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //view
    private ImageSlider image_slider;
    private LinearLayout btn_tat_ca, btn_viet_nam, btn_quoc_te;

    private RoundedImageView img_categories;

    //variable
    private Items mSong;
    private boolean isPlaying;
    private int action;
    private NewRelease newRelease;
    private final DataHomeAll dataHomeAll = new DataHomeAll();
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;


    //init_view

    // rv_new_release_song
    private RecyclerView rv_new_release_song;
    private SongAllAdapter new_release_songAdapter;
    private ArrayList<Items> new_release_songArrayList = new ArrayList<>();


    // bxh_new_release_song
    private SongMoreAdapter bxh_new_release_songAdapter;
    private ArrayList<Items> bxh_new_release_songArrayList = new ArrayList<>();


    // bxh nhac
    private SongMoreAdapter bang_xep_hangAdapter;
    private ArrayList<Items> bang_xep_hangArrayList = new ArrayList<>();


    //top100
    private Top100MoreAdapter top100MoreAdapter;
    private ArrayList<DataPlaylist> dataPlaylistArrayListTop100 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Helper.changeStatusBarColor(this, R.color.black);
        Helper.changeNavigationColor(this, R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        musicHelper = new MusicHelper(this, sharedPreferencesManager);

        //init_view
        rv_new_release_song = findViewById(R.id.rv_new_release_song);
        RecyclerView rv_bxh_new_release_song = findViewById(R.id.rv_bxh_new_release_song);
        RecyclerView rv_bang_xep_hang = findViewById(R.id.rv_bang_xep_hang);
        RecyclerView rv_top100 = findViewById(R.id.rv_top100);


        img_categories = findViewById(R.id.img_categories);
        LinearLayout linear_bxh_new_release_song = findViewById(R.id.linear_bxh_new_release_song);


        ImageView img_history = findViewById(R.id.img_history);
        image_slider = findViewById(R.id.image_slider);

        ImageView img_search = findViewById(R.id.img_search);
        ImageView img_account = findViewById(R.id.img_account);


        //btn nhac moi phat hanh
        btn_tat_ca = findViewById(R.id.btn_tat_ca);
        btn_viet_nam = findViewById(R.id.btn_viet_nam);
        btn_quoc_te = findViewById(R.id.btn_quoc_te);


        // Khoi tạo RecyclerView và Adapter
        GridLayoutManager layoutManagerNhacMoi = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_new_release_song.setLayoutManager(layoutManagerNhacMoi);

        GridLayoutManager layoutManagerChonNhanh = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_bxh_new_release_song.setLayoutManager(layoutManagerChonNhanh);

        GridLayoutManager layoutManagerBangXepHang = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_bang_xep_hang.setLayoutManager(layoutManagerBangXepHang);

        LinearLayoutManager layoutManagerNoiBat = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_top100.setLayoutManager(layoutManagerNoiBat);


        // Khoi tạo Adapter

        //new_release_song
        new_release_songAdapter = new SongAllAdapter(new_release_songArrayList, MainActivity.this, MainActivity.this);
        rv_new_release_song.setAdapter(new_release_songAdapter);

        bxh_new_release_songAdapter = new SongMoreAdapter(bxh_new_release_songArrayList, MainActivity.this, MainActivity.this);
        rv_bxh_new_release_song.setAdapter(bxh_new_release_songAdapter);

        bang_xep_hangAdapter = new SongMoreAdapter(bang_xep_hangArrayList, MainActivity.this, MainActivity.this);
        rv_bang_xep_hang.setAdapter(bang_xep_hangAdapter);

        top100MoreAdapter = new Top100MoreAdapter(dataPlaylistArrayListTop100, MainActivity.this);
        rv_top100.setAdapter(top100MoreAdapter);


        //init_bottom_view_player
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

        //onclick
        img_search.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        img_history.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        img_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetProfile bottomSheetProfile = new BottomSheetProfile(MainActivity.this, MainActivity.this);
                bottomSheetProfile.show(getSupportFragmentManager(), bottomSheetProfile.getTag());
            }
        });

        linear_bxh_new_release_song.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BXHNewSongActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("new_release_song", newRelease);
            intent.putExtras(bundle);

            startActivity(intent);
        });

        //btn nhac moi phat hanh
        btn_viet_nam.setOnClickListener(view -> checkCategoriesNewReleaseSong(1));
        btn_quoc_te.setOnClickListener(view -> checkCategoriesNewReleaseSong(2));
        btn_tat_ca.setOnClickListener(view -> checkCategoriesNewReleaseSong(0));


        //get data
        getBXH();
        getTop100();
        getNewRelease();
        getHome();
//        getAlbum();
    }

    private void getNewRelease() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories(null, null);
                    Map<String, String> map = chartCategories.getNewReleaseChart();

                    retrofit2.Call<NewRelease> call = service.NEW_RELEASE_CALL(map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<NewRelease>() {
                        @Override
                        public void onResponse(@NonNull Call<NewRelease> call, @NonNull Response<NewRelease> response) {
                            if (response.isSuccessful()) {
                                Log.d("getNewRelese", call.request().url().toString());
                                newRelease = response.body();
                                if (newRelease != null && newRelease.getErr() == 0) {
                                    ArrayList<Items> itemsArrayList = newRelease.getData().getItems();
                                    if (!itemsArrayList.isEmpty()) {
                                        runOnUiThread(() -> {
                                            bxh_new_release_songArrayList = itemsArrayList;
                                            bxh_new_release_songAdapter.setFilterList(itemsArrayList);
                                            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bxh_new_release_songArrayList, bxh_new_release_songAdapter);
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
                        public void onFailure(@NonNull Call<NewRelease> call, @NonNull Throwable throwable) {

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

    private void getHome() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories(null, null);
                    Map<String, String> map = chartCategories.getHome(1);

                    retrofit2.Call<ResponseBody> call = service.HOME_CALL(map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            String requestUrl = call.request().url().toString();
                            Log.d(">>>>>>>>>>>>>>>>>>>", "HOme - " + requestUrl);
                            if (response.isSuccessful()) {
                                try {
                                    String jsonData = response.body().string();
                                    JSONObject jsonObject = new JSONObject(jsonData);
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    JSONArray itemsArray = dataObject.getJSONArray("items");

                                    // Kiểm tra xem mảng items có ít nhất một phần tử không
                                    if (itemsArray.length() > 0) {
                                        // Lấy phần tử đầu tiên từ mảng items
                                        JSONObject firstItemObject = itemsArray.getJSONObject(0);

                                        // Tạo một đối tượng DataHome
                                        DataHomeSlider dataHomeSlider = new DataHomeSlider();
                                        dataHomeSlider.setSectionType(firstItemObject.getString("sectionType"));
                                        dataHomeSlider.setViewType(firstItemObject.getString("viewType"));
                                        dataHomeSlider.setTitle(firstItemObject.getString("title"));
                                        dataHomeSlider.setLink(firstItemObject.getString("link"));
                                        dataHomeSlider.setSectionId(firstItemObject.getString("sectionId"));

                                        // Tạo một mảng mới để chứa các đối tượng ItemSilder từ mảng "items" bên trong item đầu tiên
                                        JSONArray innerItemsArray = firstItemObject.getJSONArray("items");
                                        ArrayList<ItemSlider> innerItemSliders = new ArrayList<>();
                                        for (int i = 0; i < innerItemsArray.length(); i++) {
                                            JSONObject innerItemObject = innerItemsArray.getJSONObject(i);
                                            ItemSlider itemSlider = ItemSlider.fromJson(innerItemObject.toString());
                                            innerItemSliders.add(itemSlider);
                                        }

                                        // Đặt mảng innerItemSliders là mảng items của DataHome
                                        dataHomeSlider.setItems(innerItemSliders);


                                        runOnUiThread(() -> {
                                            ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
                                            // Lặp qua các phần tử trong dataHome.getItems() và thêm chúng vào slideModelArrayList
                                            for (int i = 0; i < dataHomeSlider.getItems().size(); i++) {
                                                // Kiểm tra xem vòng lặp đã đủ số lượng phần tử cần thiết chưa
                                                slideModelArrayList.add(new SlideModel(dataHomeSlider.getItems().get(i).getBanner(), ScaleTypes.FIT));
                                            }
                                            image_slider.setImageList(slideModelArrayList);
                                        });


                                        ////


                                        JSONObject firstItemObject2 = itemsArray.getJSONObject(2);

                                        // Tạo một đối tượng DataHome
                                        dataHomeAll.setSectionType(firstItemObject2.getString("sectionType"));
                                        dataHomeAll.setTitle(firstItemObject2.getString("title"));
                                        dataHomeAll.setLink(firstItemObject2.getString("link"));

                                        // Tạo một mảng mới để chứa các đối tượng ItemSilder từ mảng "items" bên trong item đầu tiên
                                        JSONObject dataObject2 = firstItemObject2.getJSONObject("items");

                                        // Lấy mảng JSON tương ứng với từng loại items
                                        JSONArray allArray = dataObject2.getJSONArray("all");
                                        JSONArray vPopArray = dataObject2.getJSONArray("vPop");
                                        JSONArray otherArray = dataObject2.getJSONArray("others");

                                        ArrayList<Items> allItems = new ArrayList<>();
                                        ArrayList<Items> vPopItems = new ArrayList<>();
                                        ArrayList<Items> otherItems = new ArrayList<>();

                                        for (int i = 0; i < allArray.length(); i++) {
                                            JSONObject innerItemObject = allArray.getJSONObject(i);
                                            Items item = Items.fromJson(innerItemObject.toString());
                                            allItems.add(item);
                                        }
                                        for (int i = 0; i < vPopArray.length(); i++) {
                                            JSONObject innerItemObject = vPopArray.getJSONObject(i);
                                            Items item = Items.fromJson(innerItemObject.toString());
                                            vPopItems.add(item);
                                        }
                                        for (int i = 0; i < otherArray.length(); i++) {
                                            JSONObject innerItemObject = otherArray.getJSONObject(i);
                                            Items item = Items.fromJson(innerItemObject.toString());
                                            otherItems.add(item);
                                        }


                                        ItemsData itemsData = new ItemsData();
                                        itemsData.setAll(allItems);
                                        itemsData.setvPop(vPopItems);
                                        itemsData.setOthers(otherItems);

                                        // Đặt mảng innerItemSliders là mảng items của DataHome
                                        dataHomeAll.setItems(itemsData);
                                        runOnUiThread(() -> {
                                            new_release_songArrayList = dataHomeAll.getItems().getAll();
                                            new_release_songAdapter.setFilterList(new_release_songArrayList);
                                            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), new_release_songArrayList, new_release_songAdapter);
                                        });

                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Log.d("TAG", "Failed to retrieve data: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {

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

    private void getBXH() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories(null, null);
                    Map<String, String> map = chartCategories.getChartHome();

                    retrofit2.Call<ChartHome> call = service.CHART_HOME_CALL(map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ChartHome>() {
                        @Override
                        public void onResponse(@NonNull Call<ChartHome> call, @NonNull Response<ChartHome> response) {
                            if (response.isSuccessful()) {
                                Log.d("getBXH", call.request().url().toString());
                                ChartHome chartHome = response.body();
                                if (chartHome != null && chartHome.getErr() == 0) {
                                    ArrayList<Items> itemsArrayList = chartHome.getData().getRTChart().getItems();
                                    if (!itemsArrayList.isEmpty()) {
                                        runOnUiThread(() -> {
                                            bang_xep_hangArrayList = itemsArrayList;
                                            bang_xep_hangAdapter.setFilterList(itemsArrayList);
                                            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bang_xep_hangArrayList, bang_xep_hangAdapter);
                                            Glide.with(MainActivity.this)
                                                    .load(chartHome.getData().getWeekChart().getVn().getCover())
                                                    .into(img_categories);

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
                        public void onFailure(@NonNull Call<ChartHome> call, @NonNull Throwable throwable) {

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

    private void getTop100() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories(null, null);
                    Map<String, String> map = chartCategories.getTop100();

                    retrofit2.Call<Top100> call = service.TOP100_CALL(map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Top100>() {
                        @Override
                        public void onResponse(@NonNull Call<Top100> call, @NonNull Response<Top100> response) {
                            if (response.isSuccessful()) {
                                Log.d("getTop100", call.request().url().toString());
                                Top100 top100 = response.body();
                                if (top100 != null && top100.getErr() == 0) {
                                    ArrayList<DataPlaylist> itemsTop100sNoiBat = top100.getDataTop100().get(0).getItems();
                                    if (!itemsTop100sNoiBat.isEmpty()) {
                                        runOnUiThread(() -> {
                                            dataPlaylistArrayListTop100 = itemsTop100sNoiBat;
                                            top100MoreAdapter.setFilterList(itemsTop100sNoiBat);
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
                        public void onFailure(@NonNull Call<Top100> call, @NonNull Throwable throwable) {

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

//    private void getAlbum() {
//        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
//            @Override
//            public void onServiceCreated(ApiService service) {
//                try {
//                    SongCategories songCategories = new SongCategories(null, null);
//                    Map<String, String> map = songCategories.getPlaylist("SC0708EF");
//
//                    retrofit2.Call<Playlist> call = service.PLAYLIST_CALL("SC0708EF", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
//                    call.enqueue(new Callback<Playlist>() {
//                        @Override
//                        public void onResponse(@NonNull Call<Playlist> call, @NonNull Response<Playlist> response) {
//                            String requestUrl = call.request().url().toString();
//                            Log.d(">>>>>>>>>>>>>>>>>>>", " - " + requestUrl);
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<Playlist> call, @NonNull Throwable throwable) {
//
//                        }
//                    });
//                } catch (Exception e) {
//                    Log.e("TAG", "Error: " + e.getMessage(), e);
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
//    }

    @SuppressLint("NotifyDataSetChanged")
    private void checkCategoriesNewReleaseSong(int categories_nhac_moi) {
        new_release_songArrayList.clear();
        switch (categories_nhac_moi) {
            case 1:
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories_check);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories);

                new_release_songArrayList.addAll(dataHomeAll.getItems().getvPop());

                break;
            case 2:
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories_check);

                new_release_songArrayList.addAll(dataHomeAll.getItems().getOthers());
                break;
            default:
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories_check);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories);

                new_release_songArrayList.addAll(dataHomeAll.getItems().getAll());
                break;
        }
        new_release_songAdapter.setFilterList(new_release_songArrayList);
        new_release_songAdapter.notifyDataSetChanged();
        rv_new_release_song.scrollToPosition(0);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), new_release_songArrayList, new_release_songAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicHelper.registerReceivers();

        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), new_release_songArrayList, new_release_songAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bxh_new_release_songArrayList, bxh_new_release_songAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), bang_xep_hangArrayList, bang_xep_hangAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }
}