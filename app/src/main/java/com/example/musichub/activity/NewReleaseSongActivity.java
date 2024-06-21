package com.example.musichub.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.musichub.R;
import com.example.musichub.adapter.new_release_song.NewReleaseViewPageAdapter;
import com.example.musichub.helper.ui.Helper;
import com.google.android.material.tabs.TabLayout;

public class NewReleaseSongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_release_song);
        Helper.changeStatusBarColor(NewReleaseSongActivity.this, R.color.black);
        Helper.changeNavigationColor(this, R.color.black, true);

        TabLayout tab_layout_new_release_song = findViewById(R.id.tab_layout_new_release_song);
        ViewPager view_pager_new_release_song = findViewById(R.id.view_pager_new_release_song);

        NewReleaseViewPageAdapter mViewPagerAdapter = new NewReleaseViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        view_pager_new_release_song.setAdapter(mViewPagerAdapter);

        tab_layout_new_release_song.setupWithViewPager(view_pager_new_release_song);

        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(view -> finish());

    }

}