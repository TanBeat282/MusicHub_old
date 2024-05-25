package com.example.musichub.fragment.NewReleaseSong;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.activity.NewReleaseSongActivity;
import com.example.musichub.activity.ViewPlaylistActivity;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.model.chart.chart_home.ChartHome;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.new_release.NewReleaseSong;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongFragment extends Fragment {
    private RecyclerView recycler_view_song;
    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private SongAllAdapter songAllAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler_view_song = view.findViewById(R.id.recycler_view_song);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recycler_view_song.setLayoutManager(layoutManager);
        songAllAdapter = new SongAllAdapter(itemsArrayList, requireActivity(), requireContext());
        recycler_view_song.setAdapter(songAllAdapter);


        getNewReleaseSong("song");
    }

    private void getNewReleaseSong(String type) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getNewRelease(type);

                    retrofit2.Call<NewReleaseSong> call = service.NEW_RELEASE_SONG_CALL(type, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<NewReleaseSong>() {
                        @Override
                        public void onResponse(Call<NewReleaseSong> call, Response<NewReleaseSong> response) {
                            if (response.isSuccessful()) {
                                Log.d(">>>>>>>>>>>>>>>>>>", "getNewReleaseSong " + call.request().url());
                                NewReleaseSong newReleaseSong = response.body();
                                if (newReleaseSong != null && newReleaseSong.getErr() == 0) {
                                    ArrayList<Items> arrayList = newReleaseSong.getData();
                                    if (!arrayList.isEmpty()) {
                                        requireActivity().runOnUiThread(() -> {
                                            itemsArrayList = arrayList;
                                            songAllAdapter.setFilterList(itemsArrayList);
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
                        public void onFailure(Call<NewReleaseSong> call, Throwable throwable) {

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