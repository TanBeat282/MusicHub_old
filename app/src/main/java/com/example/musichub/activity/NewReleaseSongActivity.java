package com.example.musichub.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.musichub.R;
import com.example.musichub.adapter.new_release_song.NewReleaseViewPageAdapter;
import com.example.musichub.helper.ui.Helper;
import com.google.android.material.tabs.TabLayout;

public class NewReleaseSongActivity extends AppCompatActivity {
    private TabLayout tab_layout_new_release_song;
    private ViewPager view_pager_new_release_song;
    private NewReleaseViewPageAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_release_song);
        Helper.changeStatusBarColor(NewReleaseSongActivity.this, R.color.black);

        tab_layout_new_release_song = findViewById(R.id.tab_layout_new_release_song);
        view_pager_new_release_song = findViewById(R.id.view_pager_new_release_song);

        mViewPagerAdapter = new NewReleaseViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        view_pager_new_release_song.setAdapter(mViewPagerAdapter);

        tab_layout_new_release_song.setupWithViewPager(view_pager_new_release_song);

    }

}