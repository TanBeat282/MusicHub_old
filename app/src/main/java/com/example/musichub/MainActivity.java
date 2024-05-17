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
import com.example.musichub.activity.HistoryActivity;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.activity.SearchActivity;
import com.example.musichub.adapter.BaiHatNhanhAdapter;
import com.example.musichub.adapter.Top100Adapter;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.adapter.VideoAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.ChartCategories;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
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
import com.github.kiulian.downloader.YoutubeDownloader;
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
    private ImageView img_play_pause, img_history;
    private ImageSlider image_slider;
    private RoundedImageView img_album_song;
    private LinearLayout layoutPlayer, linear_play_pause, linear_next;
    private LinearLayout btn_tat_ca, btn_viet_nam, btn_quoc_te;
    private RecyclerView rv_nhac_moi;
    private DataHomeAll dataHomeAll = new DataHomeAll();
    private TextView tvTitleSong, tvSingleSong;
    private LinearProgressIndicator progressIndicator;
    private Items mSong;
    private boolean isPlaying;
    private int action;
    private int currentTime, total_time;
    private TopSongAdapter topSongAdapter;
    private TopSongAdapter nhacMoiAdapter;
    private BaiHatNhanhAdapter baiHatNhanhAdapter;
    private VideoAdapter videoAdapter;
    private Top100Adapter top100AdapterNoiBat;
    private Top100Adapter top100AdapterVietNam;
    private Top100Adapter top100AdapterChauA;
    private Top100Adapter top100AdapterAuMy;
    private SharedPreferencesManager sharedPreferencesManager;
    private View layoutPlayerBottom;
    private ArrayList<Items> songListVideoBaiHat;
    private ArrayList<Items> songListChonNhanh;
    private ArrayList<Items> songListBangXepHang;
    private ArrayList<Items> itemsArrayListNhacMoi;
    private ArrayList<DataPlaylist> itemsTop100sNoiBat, itemsTop100sVietNam, itemsTop100sChauA, itemsTop100sAuMy;


    private RoundedImageView img_categories;

    private final YoutubeDownloader downloader = new YoutubeDownloader();

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
            checkIsPlayingTop(mSong, songListBangXepHang);
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

        Helper.changeStatusBarColor(this, R.color.black);
        Helper.changeNavigationColor(this, R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());

        rv_nhac_moi = findViewById(R.id.rv_nhac_moi);
        RecyclerView rv_chon_nhanh = findViewById(R.id.rv_chon_nhanh);
        RecyclerView rv_bang_xep_hang = findViewById(R.id.rv_bang_xep_hang);
        RecyclerView rv_noibat = findViewById(R.id.rv_noibat);
        RecyclerView rv_nhac_vietNam = findViewById(R.id.rv_nhac_vietNam);
        RecyclerView rv_nhac_chauA = findViewById(R.id.rv_nhac_chauA);
        RecyclerView rv_nhac_auMy = findViewById(R.id.rv_nhac_auMy);
        RecyclerView rv_video_bai_hat_lien_quan = findViewById(R.id.rv_video_bai_hat_lien_quan);


        img_categories = findViewById(R.id.img_categories);


        img_history = findViewById(R.id.img_history);
        image_slider = findViewById(R.id.image_slider);

        ImageView img_search = findViewById(R.id.img_search);
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


        //btn nhac moi phat hanh
        btn_tat_ca = findViewById(R.id.btn_tat_ca);
        btn_viet_nam = findViewById(R.id.btn_viet_nam);
        btn_quoc_te = findViewById(R.id.btn_quoc_te);

        itemsArrayListNhacMoi = new ArrayList<>();
        songListChonNhanh = new ArrayList<>();
        songListBangXepHang = new ArrayList<>();
        songListVideoBaiHat = new ArrayList<>();

        itemsTop100sNoiBat = new ArrayList<>();
        itemsTop100sVietNam = new ArrayList<>();
        itemsTop100sChauA = new ArrayList<>();
        itemsTop100sAuMy = new ArrayList<>();

        // Khoi tạo RecyclerView và Adapter
        GridLayoutManager layoutManagerNhacMoi = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_nhac_moi.setLayoutManager(layoutManagerNhacMoi);

        GridLayoutManager layoutManagerChonNhanh = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_chon_nhanh.setLayoutManager(layoutManagerChonNhanh);

        GridLayoutManager layoutManagerBangXepHang = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_bang_xep_hang.setLayoutManager(layoutManagerBangXepHang);

        GridLayoutManager layoutManagerVideoBaiHat = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_video_bai_hat_lien_quan.setLayoutManager(layoutManagerVideoBaiHat);


        LinearLayoutManager layoutManagerNoiBat = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_noibat.setLayoutManager(layoutManagerNoiBat);
        LinearLayoutManager layoutManagerVietNam = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_nhac_vietNam.setLayoutManager(layoutManagerVietNam);
        LinearLayoutManager layoutManagerChauA = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_nhac_chauA.setLayoutManager(layoutManagerChauA);
        LinearLayoutManager layoutManagerAuMy = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_nhac_auMy.setLayoutManager(layoutManagerAuMy);


        // Khoi tạo Adapter

        nhacMoiAdapter = new TopSongAdapter(itemsArrayListNhacMoi, MainActivity.this, MainActivity.this);
        rv_nhac_moi.setAdapter(nhacMoiAdapter);


        baiHatNhanhAdapter = new BaiHatNhanhAdapter(songListChonNhanh, MainActivity.this);
        rv_chon_nhanh.setAdapter(baiHatNhanhAdapter);


        topSongAdapter = new TopSongAdapter(songListBangXepHang, MainActivity.this, MainActivity.this);
        rv_bang_xep_hang.setAdapter(topSongAdapter);

//        videoAdapter = new VideoAdapter(songListVideoBaiHat, MainActivity.this);
//        rv_video_bai_hat_lien_quan.setAdapter(videoAdapter);


        top100AdapterNoiBat = new Top100Adapter(itemsTop100sNoiBat, MainActivity.this);
        rv_noibat.setAdapter(top100AdapterNoiBat);
        top100AdapterVietNam = new Top100Adapter(itemsTop100sVietNam, MainActivity.this);
        rv_nhac_vietNam.setAdapter(top100AdapterVietNam);
        top100AdapterChauA = new Top100Adapter(itemsTop100sChauA, MainActivity.this);
        rv_nhac_chauA.setAdapter(top100AdapterChauA);
        top100AdapterAuMy = new Top100Adapter(itemsTop100sAuMy, MainActivity.this);
        rv_nhac_auMy.setAdapter(top100AdapterAuMy);

        getSongCurrent();
        getBXH();
        getTop100();
        getNewRelese();
        getHome();
        getAlbum();

//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        layoutPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayNowActivity.class);
            startActivity(intent);
        });
        img_search.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        img_history.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));


        //btn nhac moi phat hanh
        btn_viet_nam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCategoriesNhacMoi(1);
            }
        });
        btn_quoc_te.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCategoriesNhacMoi(2);
            }
        });
        btn_tat_ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCategoriesNhacMoi(0);
            }
        });
    }

    private void getNewRelese() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories(null, null);
                    Map<String, String> map = chartCategories.getNewReleaseChart();

                    retrofit2.Call<NewRelease> call = service.NEW_RELEASE_CALL(map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<NewRelease>() {
                        @Override
                        public void onResponse(Call<NewRelease> call, Response<NewRelease> response) {
                            if (response.isSuccessful()) {
                                Log.d("getNewRelese", call.request().url().toString());
                                NewRelease newRelease = response.body();
                                if (newRelease != null && newRelease.getErr() == 0) {
                                    ArrayList<Items> itemsArrayList = newRelease.getData().getItems();
                                    if (!itemsArrayList.isEmpty()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                songListChonNhanh = itemsArrayList;
                                                baiHatNhanhAdapter.setFilterList(itemsArrayList);
                                                checkIsPlayingTop(mSong, itemsArrayList);
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
                        public void onResponse(Call<ChartHome> call, Response<ChartHome> response) {
                            if (response.isSuccessful()) {
                                Log.d("getBXH", call.request().url().toString());
                                ChartHome chartHome = response.body();
                                if (chartHome != null && chartHome.getErr() == 0) {
                                    ArrayList<Items> itemsArrayList = chartHome.getData().getRTChart().getItems();
                                    if (!itemsArrayList.isEmpty()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                songListBangXepHang = itemsArrayList;
                                                topSongAdapter.setFilterList(itemsArrayList);
                                                checkIsPlayingTop(mSong, itemsArrayList);

                                                Glide.with(MainActivity.this)
                                                        .load(chartHome.getData().getWeekChart().getVn().getCover())
                                                        .into(img_categories);

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
                        public void onFailure(Call<ChartHome> call, Throwable throwable) {

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
                        public void onResponse(Call<Top100> call, Response<Top100> response) {
                            if (response.isSuccessful()) {
                                Log.d("getTop100", call.request().url().toString());
                                Top100 top100 = response.body();
                                if (top100 != null && top100.getErr() == 0) {
                                    ArrayList<DataPlaylist> itemsTop100sNoiBat = top100.getDataTop100().get(0).getItems();
                                    ArrayList<DataPlaylist> itemsTop100sVietNam = top100.getDataTop100().get(1).getItems();
                                    ArrayList<DataPlaylist> itemsTop100sChauA = top100.getDataTop100().get(2).getItems();
                                    ArrayList<DataPlaylist> itemsTop100sAuMy = top100.getDataTop100().get(3).getItems();
                                    if (!itemsTop100sNoiBat.isEmpty() && !itemsTop100sVietNam.isEmpty() && !itemsTop100sChauA.isEmpty() && !itemsTop100sAuMy.isEmpty()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                top100AdapterNoiBat.setFilterList(itemsTop100sNoiBat);
                                                top100AdapterVietNam.setFilterList(itemsTop100sVietNam);
                                                top100AdapterChauA.setFilterList(itemsTop100sChauA);
                                                top100AdapterAuMy.setFilterList(itemsTop100sAuMy);
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
                        public void onFailure(Call<Top100> call, Throwable throwable) {

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
                                    int err = jsonObject.getInt("err");
                                    String msg = jsonObject.getString("msg");
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


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
                                                // Lặp qua các phần tử trong dataHome.getItems() và thêm chúng vào slideModelArrayList
                                                for (int i = 0; i < dataHomeSlider.getItems().size(); i++) {
                                                    // Kiểm tra xem vòng lặp đã đủ số lượng phần tử cần thiết chưa
                                                    slideModelArrayList.add(new SlideModel(dataHomeSlider.getItems().get(i).getBanner(), ScaleTypes.FIT));
                                                }
                                                image_slider.setImageList(slideModelArrayList);
                                            }
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                itemsArrayListNhacMoi = dataHomeAll.getItems().getAll();
                                                nhacMoiAdapter.setFilterList(itemsArrayListNhacMoi);
                                                checkIsPlayingTop(mSong, itemsArrayListNhacMoi);
                                            }
                                        });

                                    } else {
                                        // Xử lý khi mảng items không có phần tử nào
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Log.d("TAG", "Failed to retrieve data: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {

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

    private void getAlbum() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getPlaylist("SC0708EF");

                    retrofit2.Call<Playlist> call = service.PLAYLIST_CALL("SC0708EF", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Playlist>() {
                        @Override
                        public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                            String requestUrl = call.request().url().toString();
                            Log.d(">>>>>>>>>>>>>>>>>>>", " - " + requestUrl);
                        }

                        @Override
                        public void onFailure(Call<Playlist> call, Throwable throwable) {

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

    @SuppressLint("NotifyDataSetChanged")
    private void checkCategoriesNhacMoi(int categories_nhac_moi) {
        itemsArrayListNhacMoi.clear();
        switch (categories_nhac_moi) {
            case 1:
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories_check);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories);

                itemsArrayListNhacMoi.addAll(dataHomeAll.getItems().getvPop());

                break;
            case 2:
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories_check);

                itemsArrayListNhacMoi.addAll(dataHomeAll.getItems().getOthers());
                break;
            default:
                btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories_check);
                btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
                btn_quoc_te.setBackgroundResource(R.drawable.background_button_categories);

                itemsArrayListNhacMoi.addAll(dataHomeAll.getItems().getAll());
                break;
        }
        nhacMoiAdapter.setFilterList(itemsArrayListNhacMoi);
        nhacMoiAdapter.notifyDataSetChanged();
        rv_nhac_moi.scrollToPosition(0);
        checkIsPlayingTop(mSong, itemsArrayListNhacMoi);
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
                .load(mSong.getThumbnail())
                .into(img_album_song);
        tvTitleSong.setText(mSong.getTitle());
        tvSingleSong.setText(mSong.getArtistsNames());

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
        int color = getResources().getColor(R.color.gray);
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        ViewCompat.setBackgroundTintList(layoutPlayer, colorStateList);

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
//        getBaiHatNhanh(mSong);
//        new SearchTask().execute();
        handleLayoutMusic(action);
    }

//    @SuppressLint("StaticFieldLeak")
//    private class SearchTask extends AsyncTask<Void, Void, SearchResult> {
//        @Override
//        protected SearchResult doInBackground(Void... voids) {
//            RequestSearchResult request = new RequestSearchResult(mSong.getTitle())
//                    .filter(
//                            TypeField.VIDEO,
//                            FormatField.HD);
//
//            return downloader.search(request).data();
//        }
//
//        @Override
//        protected void onPostExecute(SearchResult result) {
//            List<SearchResultVideoDetails> videos = result.videos();
//
//            for (SearchResultVideoDetails searchResultVideoDetails : videos) {
//                new GetVideoInfoTask().execute(searchResultVideoDetails.videoId());
//            }
//        }
//    }

    //    private class GetVideoInfoTask extends AsyncTask<String, Void, VideoInfo> {
//        @Override
//        protected VideoInfo doInBackground(String... videoIds) {
//            RequestVideoInfo request = new RequestVideoInfo(videoIds[0]);
//            com.github.kiulian.downloader.downloader.response.Response<VideoInfo> response = downloader.getVideoInfo(request);
//            VideoInfo info = response.data();
//            return info;
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        @Override
//        protected void onPostExecute(VideoInfo info) {
//            VideoDetails details = info.details();
//            List<VideoWithAudioFormat> videoWithAudioFormats = info.videoWithAudioFormats();
//
//            Items song = new Items();
//            song.setId(details.videoId());
//            song.setArtist(String.valueOf(details.viewCount()));
//            song.setThumb_medium(details.thumbnails().get(0));
//            song.setThumb(details.thumbnails().get(0));
//            song.setName(details.title());
//            song.setLyric(null);
//            song.setLink_audio(videoWithAudioFormats.get(1).url());
//
//            songListVideoBaiHat.add(song);
//            videoAdapter.notifyDataSetChanged();
//
//        }
//
//    }
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

    @Override
    protected void onResume() {
        super.onResume();
//        new SearchTask().execute();
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