package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichub.R;
import com.example.musichub.adapter.Artist.SelectArtistAdapter;
import com.example.musichub.adapter.Playlist.PlaylistAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.playlist.DataPlaylist;

import java.util.ArrayList;

public class ViewAllPlaylistActivity extends AppCompatActivity {
    private RelativeLayout relative_header;
    private ImageView img_back;
    private TextView txt_name_artist, txt_view;
    private ImageView img_more;
    private NestedScrollView nested_scroll;
    private TextView txt_playlist;
    private RecyclerView rv_playlist;
    private ArrayList<DataPlaylist> dataPlaylistArrayList = new ArrayList<>();
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_playlist);
        Helper.changeStatusBarColor(this, R.color.black);
        initViews();
        conFigViews();
        initAdapter();
        getBundle();
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataPlaylistArrayList = (ArrayList<DataPlaylist>) bundle.getSerializable("data_playlist_arraylist");
            playlistAdapter.setFilterList(dataPlaylistArrayList);
        }
    }

    private void initViews() {
        relative_header = findViewById(R.id.relative_header);
        img_back = findViewById(R.id.img_back);
        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_view = findViewById(R.id.txt_view);
        img_more = findViewById(R.id.img_more);
        nested_scroll = findViewById(R.id.nested_scroll);
        txt_playlist = findViewById(R.id.txt_playlist);
        rv_playlist = findViewById(R.id.rv_playlist);

    }

    @SuppressLint("SetTextI18n")
    private void conFigViews() {
        txt_playlist.setText("Nghệ sĩ đóng góp");

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
                    txt_name_artist.setText("Nghệ sĩ đóng góp");
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewAllPlaylistActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewAllPlaylistActivity.this, R.color.gray);
                }
            }
        });
    }

    private void initAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns
        rv_playlist.setLayoutManager(gridLayoutManager);
        playlistAdapter = new PlaylistAdapter(dataPlaylistArrayList, ViewAllPlaylistActivity.this, ViewAllPlaylistActivity.this);
        rv_playlist.setAdapter(playlistAdapter);
    }

}