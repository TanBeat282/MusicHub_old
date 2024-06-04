package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichub.R;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class ViewAllSongActivity extends AppCompatActivity {
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;

    private ArrayList<Items> itemsArrayList = new ArrayList<>();
    private SongAllAdapter songAllAdapter;
    private String name;

    private NestedScrollView nested_scroll;
    private RelativeLayout relative_header;
    private ImageView img_back, img_more;
    private TextView txt_name_artist, txt_view;
    private TextView txt_song_of_artist;
    private LinearLayout linear_filter_song;
    private LinearLayout linear_play_song;
    private TextView txt_filter_song;
    private ImageView img_filter_song;
    private RecyclerView rv_song_of_artist;
    private View layoutPlayerBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Helper.changeStatusBarColor(ViewAllSongActivity.this, R.color.black);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        musicHelper = new MusicHelper(this, sharedPreferencesManager);

        initView();
        configView();
        initBottomPlayer();
        initAdapter();
        onClick();
        getBundleSong();

    }

    private void initView() {
        nested_scroll = findViewById(R.id.nested_scroll);

        relative_header = findViewById(R.id.relative_header);
        img_back = findViewById(R.id.img_back);
        img_more = findViewById(R.id.img_more);

        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_view = findViewById(R.id.txt_view);
        txt_song_of_artist = findViewById(R.id.txt_song_of_artist);

        linear_filter_song = findViewById(R.id.linear_filter_song);
        linear_play_song = findViewById(R.id.linear_play_song);
        txt_filter_song = findViewById(R.id.txt_filter_song);
        img_filter_song = findViewById(R.id.img_filter_song);

        rv_song_of_artist = findViewById(R.id.rv_song_of_artist);

        layoutPlayerBottom = findViewById(R.id.layoutPlayerBottom);
    }

    private void configView() {
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
                    txt_name_artist.setText(name);
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewAllSongActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewAllSongActivity.this, R.color.gray);
                }
            }
        });
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_song_of_artist.setLayoutManager(linearLayoutManager);

        songAllAdapter = new SongAllAdapter(itemsArrayList, ViewAllSongActivity.this, ViewAllSongActivity.this);
        rv_song_of_artist.setAdapter(songAllAdapter);
    }

    private void onClick() {
        img_back.setOnClickListener(view1 -> finish());
        linear_play_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemsArrayList.get(0).getStreamingStatus() == 2) {
                    Toast.makeText(ViewAllSongActivity.this, "Không thể phát bài hát Premium!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ViewAllSongActivity.this, PlayNowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("song", itemsArrayList.get(0));
                    bundle.putInt("position_song", 0);
                    bundle.putSerializable("song_list", itemsArrayList);
                    bundle.putInt("title_now_playing", 0);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            }
        });
    }

    private void initBottomPlayer() {
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
        musicHelper.initAdapter(songAllAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllAdapter);
    }

    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<Items> arrayList = (ArrayList<Items>) bundle.getSerializable("song_arraylist");
            name = bundle.getString("name_artist");
            if (arrayList != null) {
                Log.d(">>>>>>>>>>>>>>", "getBundleSong: " + arrayList.size());
                txt_song_of_artist.setText(name);
                itemsArrayList = arrayList;
                songAllAdapter.setFilterList(arrayList);
                musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllAdapter);

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        musicHelper.registerReceivers();
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), itemsArrayList, songAllAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }
}