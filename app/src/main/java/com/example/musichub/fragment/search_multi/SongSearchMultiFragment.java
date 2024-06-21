package com.example.musichub.fragment.search_multi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musichub.R;
import com.example.musichub.adapter.Song.PaginationScrollListener;
import com.example.musichub.adapter.Song.SongAllMoreAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.constants.Constants;
import com.example.musichub.helper.uliti.log.LogUtil;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.search.search_multil.SearchMulti;
import com.example.musichub.model.search.song.SearchSong;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongSearchMultiFragment extends Fragment {
    private static final String ARG_DATA = "query";
    private static final String allowCorrect = "1";
    private static final int TYPE = 1;
    private String query;

    private RecyclerView rv_search_song;
    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private SongAllMoreAdapter songAllMoreAdapter;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    private int totalPages;


    private final BroadcastReceiver broadcastReceiverSearchMulti = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            query = bundle.getString("query");
            isLoading = false;
            isLastPage = false;
            currentPage = 1;
            totalPages = 0;
            searchSong(query, TYPE, currentPage);
        }
    };


    public static SongSearchMultiFragment newInstance(String data) {
        SongSearchMultiFragment fragment = new SongSearchMultiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_DATA);
            searchSong(query, TYPE, currentPage);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_search_multi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initAdapter();
    }

    private void initViews(View view) {
        rv_search_song = view.findViewById(R.id.rv_search_song);
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_search_song.setLayoutManager(linearLayoutManager);
        songAllMoreAdapter = new SongAllMoreAdapter(itemsArrayList, requireActivity(), requireContext());
        rv_search_song.setAdapter(songAllMoreAdapter);

        rv_search_song.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                new Handler().postDelayed(() -> searchSongMore(query, TYPE, currentPage), 300);
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

    public int calculateTotalPages(int totalItems) {
        return (int) Math.ceil((double) totalItems / 18);
    }

    private void searchSong(String query, int type, int currentPage) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SearchCategories searchCategories = new SearchCategories();
                    Map<String, String> map = searchCategories.getResultByType(query, type, currentPage);

                    retrofit2.Call<ResponseBody> call = service.SEARCH_TYPE_CALL(map.get("q"), map.get("type"), map.get("page"), map.get("count"), allowCorrect, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            LogUtil.d(Constants.TAG, "searchSong: " + call.request().url());
                            if (response.isSuccessful()) {
                                try {
                                    Gson gson = new Gson(); // Initialize Gson parser
                                    String json = response.body().string();
                                    SearchSong searchSong = gson.fromJson(json, SearchSong.class);

                                    if (searchSong != null && searchSong.getData() != null) {
                                        itemsArrayList.clear(); // Clear existing items
                                        itemsArrayList.addAll(searchSong.getData().getItems());
                                        requireActivity().runOnUiThread(() -> {
                                            totalPages = calculateTotalPages(searchSong.getData().getTotal());
                                            songAllMoreAdapter.setFilterList(itemsArrayList);

                                            if (currentPage < totalPages) {
                                                songAllMoreAdapter.addFooterLoading();
                                            } else {
                                                isLastPage = true;
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    Log.e("TAG", "Error reading response: " + e.getMessage(), e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e("TAG", "searchSong: onFailure: " + throwable.getMessage(), throwable);
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG", "onError: " + e.getMessage(), e);
            }
        });
    }

    private void searchSongMore(String query, int type, int currentPage) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SearchCategories searchCategories = new SearchCategories();
                    Map<String, String> map = searchCategories.getResultByType(query, type, currentPage);

                    retrofit2.Call<ResponseBody> call = service.SEARCH_TYPE_CALL(map.get("q"), map.get("type"), map.get("page"), map.get("count"), allowCorrect, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            LogUtil.d(Constants.TAG, "searchSongMore: " + call.request().url());
                            if (response.isSuccessful()) {
                                try {
                                    Gson gson = new Gson(); // Initialize Gson parser
                                    String json = response.body().string();
                                    SearchSong searchSong = gson.fromJson(json, SearchSong.class);

                                    if (searchSong != null && searchSong.getData() != null) {
                                        ArrayList<Items> newItems = searchSong.getData().getItems();
                                        requireActivity().runOnUiThread(() -> {
                                            itemsArrayList.addAll(newItems); // Add new items to existing list
                                            songAllMoreAdapter.notifyDataSetChanged();

                                            isLoading = false;
                                            if (currentPage < totalPages) {
                                                songAllMoreAdapter.addFooterLoading();
                                            } else {
                                                isLastPage = true;
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    Log.e("TAG", "Error reading response: " + e.getMessage(), e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e("TAG", "searchSongMore: onFailure: " + throwable.getMessage(), throwable);
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG", "onError: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        searchSong(query, TYPE, currentPage);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiverSearchMulti, new IntentFilter("search_query"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiverSearchMulti);
    }
}