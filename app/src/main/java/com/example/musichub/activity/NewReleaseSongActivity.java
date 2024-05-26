package com.example.musichub.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musichub.R;
import com.example.musichub.adapter.NewReleaseSongAdapter.ViewPageAdapter;
import com.example.musichub.adapter.SongAdapter.SongAllAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.ui.MusicHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class NewReleaseSongActivity extends AppCompatActivity {
    private TabLayout tab_layout_new_release_song;
    private ViewPager view_pager_new_release_song;
    private ViewPageAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_release_song);
        Helper.changeStatusBarColor(NewReleaseSongActivity.this, R.color.black);

        tab_layout_new_release_song = findViewById(R.id.tab_layout_new_release_song);
        view_pager_new_release_song = findViewById(R.id.view_pager_new_release_song);

        mViewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        view_pager_new_release_song.setAdapter(mViewPagerAdapter);

        tab_layout_new_release_song.setupWithViewPager(view_pager_new_release_song);

    }

}