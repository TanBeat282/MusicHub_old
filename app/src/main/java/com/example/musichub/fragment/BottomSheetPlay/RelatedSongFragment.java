package com.example.musichub.fragment.BottomSheetPlay;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musichub.R;
import com.example.musichub.adapter.Lyric.SentencesAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.uliti.GetUrlAudioHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.song.Lyric;
import com.example.musichub.model.song.Sentences;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatedSongFragment extends Fragment {

    private RecyclerView recyclerView;
    private SentencesAdapter adapter;
    private int currentTime, total_time;
    private GetUrlAudioHelper getUrlAudioHelper;
    private Items song;
    private SharedPreferencesManager sharedPreferencesManager;
    private SongCategories songCategories = new SongCategories();

    private ArrayList<Sentences> sentences;

    private final BroadcastReceiver seekBarUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentTime = intent.getIntExtra("current_time", 0);
            total_time = intent.getIntExtra("total_time", 0);
//            adapter.updateCurrentTime(currentTime);
        }
    };

    public RelatedSongFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_related_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUrlAudioHelper = new GetUrlAudioHelper();
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        song = sharedPreferencesManager.restoreSongState();

        recyclerView = view.findViewById(R.id.recyclerViewSentences);

//        getLyricUrl(song.getEncodeId());
    }

    public void getLyricUrl(String endcodeID) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    Map<String, String> map = songCategories.getLyrics(endcodeID);

                    retrofit2.Call<Lyric> call = service.LYRIC_CALL(endcodeID, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Lyric>() {
                        @Override
                        public void onResponse(Call<Lyric> call, Response<Lyric> response) {
                            if (response.isSuccessful()) {
                                Lyric lyric = response.body();
                                if (lyric != null) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sentences = lyric.getData().getSentences();
                                            adapter = new SentencesAdapter(sentences, requireContext());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                            recyclerView.setAdapter(adapter);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Lyric> call, Throwable throwable) {
                        }
                    });

                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Exception e) {
                // Xử lý lỗi ở đây
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(seekBarUpdateReceiver, new IntentFilter("send_seekbar_update"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(seekBarUpdateReceiver);
    }
}