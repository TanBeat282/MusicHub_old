package com.example.musichub.activity.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.musichub.R;
import com.example.musichub.adapter.Search.search_multi.SearchMultiViewPageAdapter;
import com.example.musichub.adapter.Search.search_suggestion.SearchSuggestionAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.service.RetrofitClient;
import com.example.musichub.api.type_adapter_Factory.search.SearchTypeAdapter;
import com.example.musichub.constants.Constants;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.helper.uliti.log.LogUtil;
import com.example.musichub.model.search.search_multil.SearchMulti;
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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchSuggestionActivity extends AppCompatActivity implements SearchSuggestionAdapter.KeyWordItemClickListener {

    private SearchView searchView;
    private ImageView img_back;

    //search suggestion
    private RelativeLayout relative_search_suggestion;
    private RecyclerView rv_search_suggestion;

    //search multi
    private RelativeLayout relative_search_multi;
    private TabLayout tab_layout_search_multi;
    private ViewPager view_pager_search_multi;
    private static final String allowCorrect = "1";

    //no data
    private LinearLayout txtNoData;
    private TextView txtNoMusic;
    private ProgressBar progress_bar_loading;


    private SearchSuggestionAdapter searchSuggestionAdapter;
    private final ArrayList<SearchSuggestionsDataItemKeyWordsItem> searchSuggestionsDataItemKeyWordsItems = new ArrayList<>();
    private final ArrayList<SearchSuggestionsDataItemSuggestionsArtist> searchSuggestionsDataItemSuggestionsArtists = new ArrayList<>();
    private final ArrayList<SearchSuggestionsDataItemSuggestionsSong> searchSuggestionsDataItemSuggestionsSongs = new ArrayList<>();
    private final ArrayList<SearchSuggestionsDataItemSuggestionsPlaylist> searchSuggestionsDataItemSuggestionsPlaylists = new ArrayList<>();
    private SharedPreferencesManager sharedPreferencesManager;
    private final Handler handler = new Handler();
    private static final int DELAY = 500;
    private Runnable searchRunnable;
    private MusicHelper musicHelper;

    private ApiService apiService;

    private final BroadcastReceiver broadcastReceiverTabLayout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            int tab_layout_position = bundle.getInt("position");
            tab_layout_search_multi.getTabAt(tab_layout_position).select();
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPreferencesManager = new SharedPreferencesManager(SearchSuggestionActivity.this);
        musicHelper = new MusicHelper(this, sharedPreferencesManager);
        Helper.changeNavigationColor(this, R.color.gray, true);
        Helper.changeStatusBarColor(this, R.color.black);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        initViewsSearchMulti();
        initViewsSearchSuggestion();
        conFigViewSearchSuggestion();


        initAdapter();
        onClick();

        searchRunnable = () -> {
            try {
                searchSuggestion(searchView.getQuery().toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

    }

    private void initViewsSearchSuggestion() {
        searchView = findViewById(R.id.searchView);
        relative_search_suggestion = findViewById(R.id.relative_search_suggestion);
        rv_search_suggestion = findViewById(R.id.rv_search_suggestion);
        txtNoData = findViewById(R.id.txtNoData);
        txtNoMusic = findViewById(R.id.txtNoMusic);
        progress_bar_loading = findViewById(R.id.progress_bar_loading);
        img_back = findViewById(R.id.img_back);
    }

    private void initViewsSearchMulti() {
        relative_search_multi = findViewById(R.id.relative_search_multi);

        tab_layout_search_multi = findViewById(R.id.tab_layout_search_multi);
        view_pager_search_multi = findViewById(R.id.view_pager_search_multi);
    }

    private void initViewPager(String query) {
        SearchMultiViewPageAdapter mSearchMultiViewPageAdapter = new SearchMultiViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, query);
        view_pager_search_multi.setAdapter(mSearchMultiViewPageAdapter);
        tab_layout_search_multi.setupWithViewPager(view_pager_search_multi);
    }

    @SuppressLint("SetTextI18n")
    private void conFigViewSearchSuggestion() {
        txtNoData.setVisibility(View.VISIBLE);
        rv_search_suggestion.setVisibility(View.GONE);
        txtNoMusic.setText("Không có bài hát nào ở đây");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initViewPager(query);
                sendBroadcast(query);
                relative_search_suggestion.setVisibility(View.GONE);
                relative_search_multi.setVisibility(View.VISIBLE);

                // Close the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                relative_search_suggestion.setVisibility(View.VISIBLE);
                relative_search_multi.setVisibility(View.GONE);

                if (newText == null || newText.isEmpty()) {
                    txtNoData.setVisibility(View.VISIBLE);
                    txtNoMusic.setVisibility(View.VISIBLE);
                    rv_search_suggestion.setVisibility(View.GONE);
                    progress_bar_loading.setVisibility(View.GONE);
                } else {
                    txtNoData.setVisibility(View.VISIBLE);
                    txtNoMusic.setVisibility(View.GONE);
                    progress_bar_loading.setVisibility(View.VISIBLE);

                    rv_search_suggestion.setVisibility(View.GONE);
                    handler.removeCallbacks(searchRunnable);
                    handler.postDelayed(searchRunnable, DELAY);
                }

                return true;
            }
        });
    }

    private void initAdapter() {
        rv_search_suggestion.setLayoutManager(new LinearLayoutManager(this));
        searchSuggestionAdapter = new SearchSuggestionAdapter(this, this, searchSuggestionsDataItemKeyWordsItems, searchSuggestionsDataItemSuggestionsArtists, searchSuggestionsDataItemSuggestionsPlaylists, searchSuggestionsDataItemSuggestionsSongs);
        rv_search_suggestion.setAdapter(searchSuggestionAdapter);
        searchSuggestionAdapter.setListener(this);
    }

    private void onClick() {
        img_back.setOnClickListener(v -> {
            finish();
        });
    }

    private void searchSuggestion(String query) throws Exception {
        // Clear previous data before making a new search request
        searchSuggestionsDataItemKeyWordsItems.clear();
        searchSuggestionsDataItemSuggestionsArtists.clear();
        searchSuggestionsDataItemSuggestionsSongs.clear();
        searchSuggestionsDataItemSuggestionsPlaylists.clear();

        // Gọi phương thức trong ApiService
        SearchCategories searchCategories = new SearchCategories();
        Map<String, String> map = searchCategories.getSearchSuggestion();

        Call<ResponseBody> call = apiService.SEARCH_SUGGESTIONS_CALL("10", query, "vi", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));

        // Thực hiện cuộc gọi bất đồng bộ
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                LogUtil.d(Constants.TAG, "searchSuggestion: " + call.request().url());
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String jsonData = response.body().string();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.registerTypeAdapter(SearchSuggestionsDataItem.class, new SearchTypeAdapter());
                        Gson gson = gsonBuilder.create();

                        SearchSuggestions searchSuggestions = gson.fromJson(jsonData, SearchSuggestions.class);

                        runOnUiThread(() -> {
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
                            txtNoData.setVisibility(View.GONE);
                            txtNoMusic.setVisibility(View.GONE);
                            progress_bar_loading.setVisibility(View.VISIBLE);
                            rv_search_suggestion.setVisibility(View.VISIBLE);
                            searchSuggestionAdapter.setFilterList(searchSuggestionsDataItemKeyWordsItems, searchSuggestionsDataItemSuggestionsArtists, searchSuggestionsDataItemSuggestionsPlaylists, searchSuggestionsDataItemSuggestionsSongs);
                        });

                    } catch (Exception e) {
                        Log.e("TAG", "Error: " + e.getMessage(), e);
                    }
                } else {
                    Log.d("TAG", "Failed to retrieve data: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Xử lý lỗi trong quá trình gọi API
                Log.e("SearchActivity", "Error fetching search results: " + t.getMessage());
            }
        });
    }

    private void sendBroadcast(String query) {
        Intent intent = new Intent("search_query");
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        LogUtil.d(Constants.TAG, "query1 " + query);
    }

    @Override
    public void onKeyWordItemClick(String keyword) {
        initViewPager(keyword);
        sendBroadcast(keyword);
        relative_search_suggestion.setVisibility(View.GONE);
        relative_search_multi.setVisibility(View.VISIBLE);
        // Close the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverTabLayout, new IntentFilter("tab_layout_position"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverTabLayout);
    }
}