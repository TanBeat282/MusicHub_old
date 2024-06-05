package com.example.musichub.fragment.WeekChart;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musichub.R;
import com.example.musichub.adapter.BXHSong.BXHSongAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.ChartCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.ItemWeekChart;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.chart.weekchart.WeekChart;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KpopFragment extends Fragment {

    private RecyclerView recycler_week_chart;
    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private BXHSongAdapter bxhSongAdapter;
    private String week;
    private String year;
    private static final String KPOP_CATEGORY_ID = "IWZ9Z0BO";
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;

    public BroadcastReceiver WeekYearBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    return;
                }
                week = bundle.getString("week_chart");
                year = bundle.getString("year_chart");
                getWeekChart(KPOP_CATEGORY_ID, week, year);
            }
        };
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kpop, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Helper.changeNavigationColor(requireActivity(), R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        musicHelper = new MusicHelper(requireContext(), sharedPreferencesManager);
        initViews(view);
        initAdapter();
        initBottomPlayer(view);
        getWeekChart(KPOP_CATEGORY_ID, "22", "2024");

    }

    private void initViews(View view) {
        recycler_week_chart = view.findViewById(R.id.recycler_week_chart);
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_week_chart.setLayoutManager(linearLayoutManager);
        bxhSongAdapter = new BXHSongAdapter(itemsArrayList, requireActivity(), requireContext());
        recycler_week_chart.setAdapter(bxhSongAdapter);
    }
    private void initBottomPlayer(View view) {
        // Khởi tạo các view
        View layoutPlayerBottom = view.findViewById(R.id.layoutPlayerBottom);
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
        musicHelper.initAdapter(bxhSongAdapter);
    }
    private void getWeekChart(String encodeId, String week, String year) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories(null, null);
                    Map<String, String> map = chartCategories.getWeekChart(encodeId);

                    retrofit2.Call<WeekChart> call = service.WEEK_CHART_CALL(encodeId, week, year, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<WeekChart>() {
                        @Override
                        public void onResponse(Call<WeekChart> call, Response<WeekChart> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>>", "getWeekChart " + call.request().url());
                            if (response.isSuccessful()) {
                                WeekChart weekChart = response.body();
                                if (weekChart != null && weekChart.getErr() == 0) {
                                    ArrayList<Items> arrayList = weekChart.getData().getItems();
                                    if (!arrayList.isEmpty()) {
                                        requireActivity().runOnUiThread(() -> {
                                            itemsArrayList = arrayList;
                                            bxhSongAdapter.setFilterList(arrayList);
                                            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, bxhSongAdapter);
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
                        public void onFailure(Call<WeekChart> call, Throwable throwable) {

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
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(WeekYearBroadcastReceiver(), new IntentFilter("send_week_year_to_fragment"));
        musicHelper.registerReceivers();
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, bxhSongAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(WeekYearBroadcastReceiver());
        musicHelper.unregisterReceivers();
    }
}
