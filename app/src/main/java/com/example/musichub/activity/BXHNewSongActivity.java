package com.example.musichub.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.musichub.R;
import com.example.musichub.adapter.BXHSong.BXHSongAdapter;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.helper.ui.BlurAndBlackOverlayTransformation;
import com.example.musichub.model.chart.chart_home.Album;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.playlist.DataPlaylist;

import java.util.ArrayList;

public class BXHNewSongActivity extends AppCompatActivity {
    private RecyclerView rv_new_release_song;
    private BXHSongAdapter bxhSongAdapter;
    private ArrayList<Items> itemsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bxhnew_song);

        rv_new_release_song = findViewById(R.id.rv_new_release_song);

        itemsArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_new_release_song.setLayoutManager(layoutManager);

        bxhSongAdapter = new BXHSongAdapter(itemsArrayList, BXHNewSongActivity.this, BXHNewSongActivity.this);
        rv_new_release_song.setAdapter(bxhSongAdapter);

        getDataBundle();
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        } else {
            itemsArrayList = (ArrayList<Items>) bundle.getSerializable("new_release_song_list");
            bxhSongAdapter.setFilterList(itemsArrayList);
        }
    }
}