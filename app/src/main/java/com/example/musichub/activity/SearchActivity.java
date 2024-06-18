package com.example.musichub.activity;

import androidx.annotation.NonNull;
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
import com.example.musichub.adapter.Search.SearchSuggestionAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.api.service.RetrofitClient;
import com.example.musichub.api.type_adapter_Factory.search.SearchTypeAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.search.search_suggestion.SearchSuggestions;
import com.example.musichub.model.search.search_suggestion.SearchSuggestionsDataItem;
import com.example.musichub.model.search.search_suggestion.keyword.SearchSuggestionsDataItemKeyWords;
import com.example.musichub.model.search.search_suggestion.keyword.SearchSuggestionsDataItemKeyWordsItem;
import com.example.musichub.model.search.search_suggestion.playlist.SearchSuggestionsDataItemSuggestionsPlaylist;
import com.example.musichub.model.search.search_suggestion.suggestion.SearchSuggestionsDataItemSuggestions;
import com.example.musichub.model.search.search_suggestion.suggestion.SearchSuggestionsDataItemSuggestionsArtist;
import com.example.musichub.model.search.search_suggestion.suggestion.SearchSuggestionsDataItemSuggestionsItem;
import com.example.musichub.model.search.search_suggestion.suggestion.SearchSuggestionsDataItemSuggestionsSong;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private SearchSuggestionAdapter searchSuggestionAdapter;
    private RecyclerView recyclerView;
    private LinearLayout txtNoData;
    private ArrayList<SearchSuggestionsDataItemKeyWordsItem> searchSuggestionsDataItemKeyWordsItems = new ArrayList<>();
    private ArrayList<SearchSuggestionsDataItemSuggestionsArtist> searchSuggestionsDataItemSuggestionsArtists = new ArrayList<>();
    private ArrayList<SearchSuggestionsDataItemSuggestionsSong> searchSuggestionsDataItemSuggestionsSongs = new ArrayList<>();
    private ArrayList<SearchSuggestionsDataItemSuggestionsPlaylist> searchSuggestionsDataItemSuggestionsPlaylists = new ArrayList<>();
    private SharedPreferencesManager sharedPreferencesManager;
    private Handler handler = new Handler();
    private static final int DELAY = 1500;
    private Runnable searchRunnable;
    private MusicHelper musicHelper;

    private ApiService apiService;

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
        searchSuggestionAdapter = new SearchSuggestionAdapter(this, this, searchSuggestionsDataItemKeyWordsItems, searchSuggestionsDataItemSuggestionsArtists, searchSuggestionsDataItemSuggestionsPlaylists, searchSuggestionsDataItemSuggestionsSongs);
        recyclerView.setAdapter(searchSuggestionAdapter);

        // Khởi tạo Retrofit instance
        apiService = RetrofitClient.getClient().create(ApiService.class);


        txtNoData.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        txtNoMusic.setText("Không có bài hát nào ở đây");

        try {
            fetchDataMusic(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Trong phương thức onCreate hoặc tương tự
        searchRunnable = new Runnable() {
            @Override
            public void run() {
                // Thực hiện công việc tìm kiếm ở đây
                try {
                    fetchDataMusic(searchView.getQuery().toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    fetchDataMusic(query);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacks(searchRunnable);
                handler.postDelayed(searchRunnable, DELAY);
                return true;
            }
        });
    }

    private void fetchDataMusic(String textSearch) throws Exception {
        if (textSearch == null || textSearch.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            callSearchApi(textSearch);
        }
    }

    private void callSearchApi(String query) throws Exception {
        // Clear previous data before making a new search request
        searchSuggestionsDataItemKeyWordsItems.clear();
        searchSuggestionsDataItemSuggestionsArtists.clear();
        searchSuggestionsDataItemSuggestionsSongs.clear();
        searchSuggestionsDataItemSuggestionsPlaylists.clear();

        // Gọi phương thức trong ApiService
        SearchCategories searchCategories = new SearchCategories();
        Map<String, String> map = searchCategories.getSuggestion();

        Call<ResponseBody> call = apiService.SEARCH_SUGGESTIONS_CALL("10", query, "vi", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));

        // Thực hiện cuộc gọi bất đồng bộ
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(">>>>>>>>>>>>>", "onResponse: " + call.request().url());
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String jsonData = response.body().string();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.registerTypeAdapter(SearchSuggestionsDataItem.class, new SearchTypeAdapter());
                        Gson gson = gsonBuilder.create();

                        SearchSuggestions searchSuggestions = gson.fromJson(jsonData, SearchSuggestions.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<SearchSuggestionsDataItem> items = searchSuggestions.getData().getItems();
                                for (SearchSuggestionsDataItem item : items) {
                                    if (item instanceof SearchSuggestionsDataItemKeyWords) {
                                        SearchSuggestionsDataItemKeyWords searchSuggestionsDataItemKeyWords = (SearchSuggestionsDataItemKeyWords) item;
                                        searchSuggestionsDataItemKeyWordsItems.addAll(searchSuggestionsDataItemKeyWords.getKeywords());

                                    } else if (item instanceof SearchSuggestionsDataItemSuggestions) {
                                        SearchSuggestionsDataItemSuggestions searchSuggestionsDataItemSuggestions = (SearchSuggestionsDataItemSuggestions) item;

                                        ArrayList<SearchSuggestionsDataItemSuggestionsItem> suggestions = searchSuggestionsDataItemSuggestions.getSuggestions();
                                        for (SearchSuggestionsDataItemSuggestionsItem item2 : suggestions) {
                                            if (item2 instanceof SearchSuggestionsDataItemSuggestionsArtist) {
                                                SearchSuggestionsDataItemSuggestionsArtist searchSuggestionsDataItemSuggestionsArtist = (SearchSuggestionsDataItemSuggestionsArtist) item2;
                                                searchSuggestionsDataItemSuggestionsArtists.add(searchSuggestionsDataItemSuggestionsArtist);
                                            } else if (item2 instanceof SearchSuggestionsDataItemSuggestionsSong) {
                                                SearchSuggestionsDataItemSuggestionsSong searchSuggestionsDataItemSuggestionsSong = (SearchSuggestionsDataItemSuggestionsSong) item2;
                                                searchSuggestionsDataItemSuggestionsSongs.add(searchSuggestionsDataItemSuggestionsSong);
                                            } else if (item2 instanceof SearchSuggestionsDataItemSuggestionsPlaylist) {
                                                SearchSuggestionsDataItemSuggestionsPlaylist searchSuggestionsDataItemSuggestionsPlaylist = (SearchSuggestionsDataItemSuggestionsPlaylist) item2;
                                                searchSuggestionsDataItemSuggestionsPlaylists.add(searchSuggestionsDataItemSuggestionsPlaylist);
                                            }
                                        }
                                    }
                                }
                                searchSuggestionAdapter.setFilterList(searchSuggestionsDataItemKeyWordsItems, searchSuggestionsDataItemSuggestionsArtists, searchSuggestionsDataItemSuggestionsPlaylists, searchSuggestionsDataItemSuggestionsSongs);
                                Log.d(">>>>>>>>>>>>", "searchSuggestionsDataItemSuggestionsArtist: " + searchSuggestionsDataItemKeyWordsItems.size());
                                Log.d(">>>>>>>>>>>>", "searchSuggestionsDataItemSuggestionsArtist: " + searchSuggestionsDataItemSuggestionsArtists.size());
                                Log.d(">>>>>>>>>>>>", "searchSuggestionsDataItemSuggestionsSongs: " + searchSuggestionsDataItemSuggestionsSongs.size());
                                Log.d(">>>>>>>>>>>>", "searchSuggestionsDataItemSuggestionsPlaylists: " + searchSuggestionsDataItemSuggestionsPlaylists.size());
                            }
                        });

                    } catch (Exception e) {
                        Log.e("TAG", "Error: " + e.getMessage(), e);
                    }
                } else {
                    Log.d("TAG", "Failed to retrieve data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Xử lý lỗi trong quá trình gọi API
                Log.e("SearchActivity", "Error fetching search results: " + t.getMessage());
            }
        });
    }


}