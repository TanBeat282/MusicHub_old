package com.example.musichub.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musichub.R;
import com.example.musichub.adapter.SearchAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.search.Search;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout txtNoData;
    private ArrayList<Items> songList;
    private Items items;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPreferencesManager = new SharedPreferencesManager(SearchActivity.this);
        items = sharedPreferencesManager.restoreSongState();

        SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        txtNoData = findViewById(R.id.txtNoData);
        TextView txtNoMusic = findViewById(R.id.txtNoMusic);

        // Khởi tạo RecyclerView và Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter
        songList = new ArrayList<>();
        adapter = new SearchAdapter(songList, this);
        recyclerView.setAdapter(adapter);

        txtNoData.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        txtNoMusic.setText("Không có bài hát nào ở đây");

        fetchDataMusic(null);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchDataMusic(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchDataMusic(newText);
                return true;
            }
        });
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
                        SearchCategories searchCategories = new SearchCategories(null, null);
                        Map<String, String> map = searchCategories.getResult(textSearch);

                        retrofit2.Call<Search> call = service.SEARCH_CALL(textSearch, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                        call.enqueue(new Callback<Search>() {
                            @Override
                            public void onResponse(Call<Search> call, Response<Search> response) {
                                if (response.isSuccessful()) {
                                    Search search = response.body();
                                    if (search != null && search.getErr() == 0) {
                                        ArrayList<Items> itemsArrayList = search.getData().getSongs();
                                        if (!itemsArrayList.isEmpty()) {
                                            runOnUiThread(() -> {
                                                runOnUiThread(() -> adapter.setFillterList(itemsArrayList));
                                                checkIsPlaying(items, itemsArrayList);
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

    private void checkIsPlaying(Items items, ArrayList<Items> songList) {
        if (items == null || songList == null) {
            return;
        }

        String currentEncodeId = items.getEncodeId();
        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
            for (Items song : songList) {
                if (currentEncodeId.equals(song.getEncodeId())) {
                    adapter.updatePlayingStatus(currentEncodeId);
                    break;
                }
            }
        }
    }

}