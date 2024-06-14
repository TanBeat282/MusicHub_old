package com.example.musichub.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musichub.R;
import com.example.musichub.adapter.Search.SearchAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.search.Search;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private LinearLayout txtNoData;
    private ArrayList<Items> searchArrayList;
    private SharedPreferencesManager sharedPreferencesManager;
    private Handler handler = new Handler();
    private static final int DELAY = 1500;
    private Runnable searchRunnable;
    private MusicHelper musicHelper;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPreferencesManager = new SharedPreferencesManager(SearchActivity.this);
        musicHelper = new MusicHelper(this, sharedPreferencesManager);
        Helper.changeNavigationColor(this, R.color.gray, true);
        Helper.changeStatusBarColor(this, R.color.black);

        SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        txtNoData = findViewById(R.id.txtNoData);
        TextView txtNoMusic = findViewById(R.id.txtNoMusic);

        // Khởi tạo RecyclerView và Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter
        searchArrayList = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchArrayList, this);
        recyclerView.setAdapter(searchAdapter);


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
        musicHelper.initAdapter(searchAdapter);


        txtNoData.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        txtNoMusic.setText("Không có bài hát nào ở đây");

        fetchDataMusic(null);

        // Trong phương thức onCreate hoặc tương tự
        searchRunnable = new Runnable() {
            @Override
            public void run() {
                // Thực hiện công việc tìm kiếm ở đây
                fetchDataMusic(searchView.getQuery().toString());
            }
        };


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchDataMusic(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacks(searchRunnable);
                handler.postDelayed(searchRunnable, DELAY);
                return true;
            }
        });
        getRecommendKeyword();
    }

    private void fetchDataMusic(String textSearch) {
        if (textSearch == null || textSearch.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
                @Override
                public void onServiceCreated(ApiService service) {
                    try {
                        SearchCategories searchCategories = new SearchCategories();
                        Map<String, String> map = searchCategories.getResult(textSearch);

                        retrofit2.Call<Search> call = service.SEARCH_CALL(textSearch, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                        call.enqueue(new Callback<Search>() {
                            @Override
                            public void onResponse(Call<Search> call, Response<Search> response) {
                                Log.d(">>>>>>>>>>>>>>>>>>", "search_getResult " + call.request().url());
                                if (response.isSuccessful()) {
                                    Search search = response.body();
                                    if (search != null && search.getErr() == 0) {
                                        ArrayList<Items> itemsArrayList = search.getData().getSongs();
                                        if (!itemsArrayList.isEmpty()) {
                                            runOnUiThread(() -> {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        searchArrayList = itemsArrayList;
                                                        searchAdapter.setFillterList(itemsArrayList);
                                                        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), searchArrayList, searchAdapter);
                                                    }
                                                });
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
                            public void onFailure(Call<Search> call, Throwable throwable) {

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
    }

    private void getRecommendKeyword() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SearchCategories searchCategories = new SearchCategories();
                    Map<String, String> map = searchCategories.getResultByType("jack", 3, 1);

                    retrofit2.Call<Search> call = service.SEARCH_TYPE_CALL(map.get("q"), map.get("type"), map.get("count"), map.get("page"), map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Search>() {
                        @Override
                        public void onResponse(Call<Search> call, Response<Search> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getRecommendKeyword " + call.request().url());
                        }

                        @Override
                        public void onFailure(Call<Search> call, Throwable throwable) {

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

    @Override
    protected void onResume() {
        super.onResume();
        musicHelper.registerReceivers();
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), searchArrayList, searchAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }


}