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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musichub.R;
import com.example.musichub.adapter.Album.AlbumAllAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Album;
import com.example.musichub.model.new_release.NewReleaseAlbum;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumFragment extends Fragment {
    private RecyclerView recycler_view_album;
    private ArrayList<Album> albumArrayList = new ArrayList<>();
    private AlbumAllAdapter albumAllAdapter;
    private MusicHelper musicHelper;
    private static final String VIETNAM_CATEGORY = "IWZ9Z08I";
    private static final String AU_MY_CATEGORY = "IWZ9Z08O";
    private static final String HAN_QUOC_CATEGORY = "IWZ9Z08W";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Helper.changeNavigationColor(requireActivity(), R.color.gray, true);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        musicHelper = new MusicHelper(requireContext(), sharedPreferencesManager);


        initializeViews(view);
        setupRecyclerView();
        setupButtonListeners();
        initBottomPlayer(view);
        getNewReleaseAlbum();
    }

    private void initializeViews(View view) {
        recycler_view_album = view.findViewById(R.id.recycler_view_album);
    }

    private void setupRecyclerView() {
        recycler_view_album.setLayoutManager(new LinearLayoutManager(requireContext()));
        albumAllAdapter = new AlbumAllAdapter(albumArrayList, requireActivity(), requireContext());
        recycler_view_album.setAdapter(albumAllAdapter);
    }

    private void setupButtonListeners() {

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
    }

    private void getNewReleaseAlbum() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getNewRelease("album");

                    retrofit2.Call<NewReleaseAlbum> call = service.NEW_RELEASE_ALBUM_CALL("album", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<NewReleaseAlbum>() {
                        @Override
                        public void onResponse(Call<NewReleaseAlbum> call, Response<NewReleaseAlbum> response) {
                            if (response.isSuccessful()) {
                                Log.d(">>>>>>>>>>>>>>>>>>", "getNewReleaseAlbum " + call.request().url());
                                NewReleaseAlbum newReleaseAlbum = response.body();
                                if (newReleaseAlbum != null && newReleaseAlbum.getErr() == 0) {
                                    ArrayList<Album> arrayList = newReleaseAlbum.getData();
                                    if (!arrayList.isEmpty()) {
                                        requireActivity().runOnUiThread(() -> {
                                            albumArrayList = arrayList;
                                            albumAllAdapter.setFilterList(albumArrayList);
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
                        public void onFailure(Call<NewReleaseAlbum> call, Throwable throwable) {

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
        musicHelper.registerReceivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }
}