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
import com.example.musichub.adapter.LichSuBaiHatAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private TextView txt_view, txt_name_artist;
    private RelativeLayout relative_header;
    private LinearLayout linear_da_nghe, linear_nghe_nhieu, linear_no_data;
    private ArrayList<Items> songListLichSuBaiHat = new ArrayList<>();
    private final ArrayList<Items> songListLichSuBaiHatNgheNhieu = new ArrayList<>();
    private LichSuBaiHatAdapter lichSuBaiHatNgheNhieuAdapter, lichSuBaiHatAdapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private MusicHelper musicHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Helper.changeStatusBarColor(this, R.color.black);
        Helper.changeNavigationColor(this, R.color.gray, true);


        sharedPreferencesManager = new SharedPreferencesManager(this);
        musicHelper = new MusicHelper(this, sharedPreferencesManager);

        relative_header = findViewById(R.id.relative_header);
        ImageView img_back = findViewById(R.id.img_back);
        txt_view = findViewById(R.id.txt_view);
        txt_name_artist = findViewById(R.id.txt_name_artist);
        NestedScrollView nested_scroll = findViewById(R.id.nested_scroll);

        linear_nghe_nhieu = findViewById(R.id.linear_nghe_nhieu);
        RecyclerView rv_history_count = findViewById(R.id.rv_history_count);
        linear_da_nghe = findViewById(R.id.linear_da_nghe);
        RecyclerView rv_history = findViewById(R.id.rv_history);
        linear_no_data = findViewById(R.id.linear_no_data);


        LinearLayoutManager layoutManagerNgheNhieu = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_history_count.setLayoutManager(layoutManagerNgheNhieu);
        lichSuBaiHatNgheNhieuAdapter = new LichSuBaiHatAdapter(songListLichSuBaiHatNgheNhieu, HistoryActivity.this);
        rv_history_count.setAdapter(lichSuBaiHatNgheNhieuAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_history.setLayoutManager(layoutManager);
        lichSuBaiHatAdapter = new LichSuBaiHatAdapter(songListLichSuBaiHat, HistoryActivity.this);
        rv_history.setAdapter(lichSuBaiHatAdapter);


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
        musicHelper.initAdapter(lichSuBaiHatNgheNhieuAdapter);
        musicHelper.initAdapter(lichSuBaiHatAdapter);

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
                    txt_name_artist.setText("Lịch sử nghe");
                    relative_header.setBackgroundColor(ContextCompat.getColor(HistoryActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(HistoryActivity.this, R.color.gray);
                }
            }
        });

        img_back.setOnClickListener(view -> finish());

        getSongHistory();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSongHistory() {
        // Khởi tạo danh sách bài hát lịch sử
        songListLichSuBaiHat = sharedPreferencesManager.restoreSongArrayListHistory();

        if (songListLichSuBaiHat.isEmpty()) {
            linear_nghe_nhieu.setVisibility(View.GONE);
            linear_da_nghe.setVisibility(View.GONE);
            linear_no_data.setVisibility(View.VISIBLE);
        } else {
            // Sắp xếp songListLichSuBaiHat theo historyCount từ lớn đến nhỏ
            songListLichSuBaiHat.sort((o1, o2) -> Integer.compare(o2.getHistoryCount(), o1.getHistoryCount()));

            // Lấy 3 mục có historyCount lớn nhất và thêm vào songListLichSuBaiHatNgheNhieu
            for (int i = 0; i < Math.min(songListLichSuBaiHat.size(), 4); i++) {
                songListLichSuBaiHatNgheNhieu.add(songListLichSuBaiHat.get(i));
            }

            // Xóa 3 mục đó ra khỏi songListLichSuBaiHat
            songListLichSuBaiHat.removeAll(songListLichSuBaiHatNgheNhieu);

            // Cập nhật Adapter và kiểm tra bài hát đang phát
            if (!songListLichSuBaiHatNgheNhieu.isEmpty()) {
                linear_nghe_nhieu.setVisibility(View.VISIBLE);
                lichSuBaiHatNgheNhieuAdapter.setFilterList(songListLichSuBaiHatNgheNhieu);
                lichSuBaiHatNgheNhieuAdapter.notifyDataSetChanged(); // Thông báo cho Adapter biết dữ liệu đã thay đổi
                musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), songListLichSuBaiHatNgheNhieu, lichSuBaiHatNgheNhieuAdapter);
            }

            // Cập nhật Adapter cho danh sách lịch sử chung và kiểm tra bài hát đang phát
            linear_da_nghe.setVisibility(View.VISIBLE);
            linear_no_data.setVisibility(View.GONE);
            lichSuBaiHatAdapter.setFilterList(songListLichSuBaiHat);
            lichSuBaiHatAdapter.notifyDataSetChanged(); // Thông báo cho Adapter biết dữ liệu đã thay đổi
            musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), songListLichSuBaiHat, lichSuBaiHatAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        songListLichSuBaiHat.clear();
        songListLichSuBaiHatNgheNhieu.clear();
        musicHelper.registerReceivers();

        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), songListLichSuBaiHatNgheNhieu, lichSuBaiHatNgheNhieuAdapter);
        musicHelper.checkIsPlayingPlaylist(sharedPreferencesManager.restoreSongState(), songListLichSuBaiHat, lichSuBaiHatAdapter);
        getSongHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHelper.unregisterReceivers();
    }
}