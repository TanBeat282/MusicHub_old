package com.example.musichub.fragment.NewReleaseSong;

import android.annotation.SuppressLint;
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
import com.example.musichub.adapter.new_release_song.NewReleaseSongAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.new_release.NewReleaseSong;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongFragment extends Fragment {
    private RecyclerView recycler_view_song;
    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private NewReleaseSongAdapter newReleaseSongAdapter;
    private LinearLayout btn_tat_ca, btn_viet_nam, btn_au_my, btn_han_quoc, btn_other;
    private SharedPreferencesManager sharedPreferencesManager;
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
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Helper.changeNavigationColor(requireActivity(), R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        musicHelper = new MusicHelper(requireContext(), sharedPreferencesManager);



        initializeViews(view);
        setupRecyclerView();
        setupButtonListeners();
        initBottomPlayer(view);
        getNewReleaseSong();
    }

    private void initializeViews(View view) {
        recycler_view_song = view.findViewById(R.id.recycler_view_song);
        btn_tat_ca = view.findViewById(R.id.btn_tat_ca);
        btn_viet_nam = view.findViewById(R.id.btn_viet_nam);
        btn_au_my = view.findViewById(R.id.btn_au_my);
        btn_han_quoc = view.findViewById(R.id.btn_han_quoc);
        btn_other = view.findViewById(R.id.btn_other);
    }

    private void setupRecyclerView() {
        recycler_view_song.setLayoutManager(new LinearLayoutManager(requireContext()));
        newReleaseSongAdapter = new NewReleaseSongAdapter(itemsArrayList, requireActivity(), requireContext());
        recycler_view_song.setAdapter(newReleaseSongAdapter);
    }

    private void setupButtonListeners() {
        btn_tat_ca.setOnClickListener(v -> checkCategoriesNewReleaseSong(0));
        btn_viet_nam.setOnClickListener(v -> checkCategoriesNewReleaseSong(1));
        btn_au_my.setOnClickListener(v -> checkCategoriesNewReleaseSong(2));
        btn_han_quoc.setOnClickListener(v -> checkCategoriesNewReleaseSong(3));
        btn_other.setOnClickListener(v -> checkCategoriesNewReleaseSong(4));
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
        musicHelper.initAdapter(newReleaseSongAdapter);
    }

    private void getNewReleaseSong() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories();
                    Map<String, String> map = songCategories.getNewRelease("song");

                    retrofit2.Call<NewReleaseSong> call = service.NEW_RELEASE_SONG_CALL("song", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
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
                                            newReleaseSongAdapter.setFilterList(itemsArrayList);
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

    @SuppressLint("NotifyDataSetChanged")
    private void checkCategoriesNewReleaseSong(int category) {
        String selectedCategory = null;
        boolean isOther = false;
        switch (category) {
            case 1:
                setButtonBackgrounds(btn_viet_nam);
                selectedCategory = VIETNAM_CATEGORY;
                break;
            case 2:
                setButtonBackgrounds(btn_au_my);
                selectedCategory = AU_MY_CATEGORY;
                break;
            case 3:
                setButtonBackgrounds(btn_han_quoc);
                selectedCategory = HAN_QUOC_CATEGORY;
                break;
            case 4:
                setButtonBackgrounds(btn_other);
                isOther = true;
                break;
            default:
                setButtonBackgrounds(btn_tat_ca);
                newReleaseSongAdapter.setFilterList(itemsArrayList); // Hiển thị tất cả dữ liệu gốc
                newReleaseSongAdapter.notifyDataSetChanged();
                recycler_view_song.scrollToPosition(0);
                return; // Kết thúc phương thức sớm để không tiếp tục xử lý bộ lọc
        }
        newReleaseSongAdapter.setFilterList(filterCategoriesSong(itemsArrayList, selectedCategory, isOther));
        newReleaseSongAdapter.notifyDataSetChanged();
        recycler_view_song.scrollToPosition(0);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, newReleaseSongAdapter);
    }

    private void setButtonBackgrounds(View selectedButton) {
        btn_tat_ca.setBackgroundResource(R.drawable.background_button_categories);
        btn_viet_nam.setBackgroundResource(R.drawable.background_button_categories);
        btn_au_my.setBackgroundResource(R.drawable.background_button_categories);
        btn_han_quoc.setBackgroundResource(R.drawable.background_button_categories);
        btn_other.setBackgroundResource(R.drawable.background_button_categories);
        selectedButton.setBackgroundResource(R.drawable.background_button_categories_check);
    }

    private ArrayList<Items> filterCategoriesSong(ArrayList<Items> itemsArrayList, String category, boolean isOther) {
        ArrayList<Items> filteredList = new ArrayList<>();
        for (Items item : itemsArrayList) {
            boolean matchesCategory = isOther ?
                    !item.getGenreIds().contains(VIETNAM_CATEGORY) &&
                            !item.getGenreIds().contains(AU_MY_CATEGORY) &&
                            !item.getGenreIds().contains(HAN_QUOC_CATEGORY) :
                    item.getGenreIds().contains(category);
            if (matchesCategory) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }
    @Override
    public void onResume() {
        super.onResume();
        musicHelper.registerReceivers();
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, newReleaseSongAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }

}