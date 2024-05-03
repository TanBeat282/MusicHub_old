package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichub.R;
import com.example.musichub.adapter.LichSuBaiHatAdapter;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HistoryActivity extends AppCompatActivity {
    private ImageView img_back, img_search;
    private RecyclerView rv_history_count, rv_history;
    private LinearLayout linear_da_nghe, linear_nghe_nhieu, linear_no_data;
    private ArrayList<Items> songListLichSuBaiHatNgheNhieu, songListLichSuBaiHat;
    private Items mSong;
    private LichSuBaiHatAdapter lichSuBaiHatNgheNhieuAdapter, lichSuBaiHatAdapter;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        img_back = findViewById(R.id.img_back);
        img_search = findViewById(R.id.img_search);

        linear_nghe_nhieu = findViewById(R.id.linear_nghe_nhieu);
        rv_history_count = findViewById(R.id.rv_history_count);
        linear_da_nghe = findViewById(R.id.linear_da_nghe);
        rv_history = findViewById(R.id.rv_history);
        linear_no_data = findViewById(R.id.linear_no_data);

        Helper.changeStatusBarColor(this, R.color.black);
        Helper.changeNavigationColor(this, R.color.gray, true);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        songListLichSuBaiHat = new ArrayList<>();
        songListLichSuBaiHatNgheNhieu = new ArrayList<>();

        LinearLayoutManager layoutManagerNgheNhieu = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_history_count.setLayoutManager(layoutManagerNgheNhieu);
        lichSuBaiHatNgheNhieuAdapter = new LichSuBaiHatAdapter(songListLichSuBaiHatNgheNhieu, HistoryActivity.this);
        rv_history_count.setAdapter(lichSuBaiHatNgheNhieuAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_history.setLayoutManager(layoutManager);
        lichSuBaiHatAdapter = new LichSuBaiHatAdapter(songListLichSuBaiHat, HistoryActivity.this);
        rv_history.setAdapter(lichSuBaiHatAdapter);

        img_back.setOnClickListener(view -> finish());


        getSongHistory();
    }

    private void checkIsPlayingHistoryNgheNhieu(Items items, ArrayList<Items> songList) {
        if (items == null || songList == null) {
            return;
        }

        String currentEncodeId = items.getEncodeId();
        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
            for (Items song : songList) {
                if (currentEncodeId.equals(song.getEncodeId())) {
                    lichSuBaiHatNgheNhieuAdapter.updatePlayingStatus(currentEncodeId);
                    break;
                }
            }
        }
    }

    private void checkIsPlayingHistory(Items items, ArrayList<Items> songList) {
        if (items == null || songList == null) {
            return;
        }

        String currentEncodeId = items.getEncodeId();
        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
            for (Items song : songList) {
                if (currentEncodeId.equals(song.getEncodeId())) {
                    lichSuBaiHatAdapter.updatePlayingStatus(currentEncodeId);
                    break;
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSongHistory() {
        // Khởi tạo danh sách bài hát lịch sử
        mSong = sharedPreferencesManager.restoreSongState();
        songListLichSuBaiHat = sharedPreferencesManager.restoreSongArrayListHistory();

        if (songListLichSuBaiHat.isEmpty()) {
            linear_nghe_nhieu.setVisibility(View.GONE);
            linear_da_nghe.setVisibility(View.GONE);
            linear_no_data.setVisibility(View.VISIBLE);
        } else {
            // Sắp xếp songListLichSuBaiHat theo historyCount từ lớn đến nhỏ
            Collections.sort(songListLichSuBaiHat, new Comparator<Items>() {
                @Override
                public int compare(Items o1, Items o2) {
                    return Integer.compare(o2.getHistoryCount(), o1.getHistoryCount());
                }
            });

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
                checkIsPlayingHistoryNgheNhieu(mSong, songListLichSuBaiHatNgheNhieu);
            }

            // Cập nhật Adapter cho danh sách lịch sử chung và kiểm tra bài hát đang phát
            linear_da_nghe.setVisibility(View.VISIBLE);
            linear_no_data.setVisibility(View.GONE);
            lichSuBaiHatAdapter.setFilterList(songListLichSuBaiHat);
            lichSuBaiHatAdapter.notifyDataSetChanged(); // Thông báo cho Adapter biết dữ liệu đã thay đổi
            checkIsPlayingHistory(mSong, songListLichSuBaiHat);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        songListLichSuBaiHat.clear();
        songListLichSuBaiHatNgheNhieu.clear();
        getSongHistory();
    }
}