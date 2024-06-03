package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichub.R;
import com.example.musichub.adapter.BXHSong.BXHSongAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.chart.new_release.NewRelease;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class BXHNewSongActivity extends AppCompatActivity {
    private RelativeLayout relative_header;
    private TextView txt_name_artist;
    private TextView txt_view;
    private TextView txt_new_release;

    private NewRelease newRelease;
    private BXHSongAdapter bxhSongAdapter;
    private ArrayList<Items> itemsArrayList;

    private MusicHelper musicHelper;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bxhnew_song);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        musicHelper = new MusicHelper(this, sharedPreferencesManager);


        NestedScrollView nested_scroll = findViewById(R.id.nested_scroll);
        ImageView img_back = findViewById(R.id.img_back);
        ImageView img_more = findViewById(R.id.img_more);

        relative_header = findViewById(R.id.relative_header);
        txt_new_release = findViewById(R.id.txt_new_release);
        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_view = findViewById(R.id.txt_view);

        RecyclerView rv_new_release_song = findViewById(R.id.rv_new_release_song);

        itemsArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_new_release_song.setLayoutManager(layoutManager);

        bxhSongAdapter = new BXHSongAdapter(itemsArrayList, BXHNewSongActivity.this, BXHNewSongActivity.this);
        rv_new_release_song.setAdapter(bxhSongAdapter);


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
        musicHelper.initAdapter(bxhSongAdapter);

        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= 200) {
                    txt_name_artist.setVisibility(View.GONE);
                    txt_view.setVisibility(View.VISIBLE);
                    relative_header.setBackgroundResource(android.R.color.transparent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
                    }

                } else if (scrollY >= 300) {
                    txt_name_artist.setVisibility(View.VISIBLE);
                    txt_view.setVisibility(View.GONE);
                    txt_name_artist.setText("BXH Nhạc Mới");
                    relative_header.setBackgroundColor(ContextCompat.getColor(BXHNewSongActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(BXHNewSongActivity.this, R.color.gray);
                }
            }
        });
        img_back.setOnClickListener(view -> finish());

        getDataBundle();
    }

    @SuppressLint("SetTextI18n")
    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemsArrayList = (ArrayList<Items>) bundle.getSerializable("bxh_new_release_song");
            if (itemsArrayList != null) {
                bxhSongAdapter.setFilterList(itemsArrayList);
                txt_new_release.setText("BXH Nhạc Mới");
                musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, bxhSongAdapter);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicHelper.registerReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }

}