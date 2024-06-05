package com.example.musichub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.musichub.R;
import com.example.musichub.adapter.WeekChart.WeekChartViewPageAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.ItemWeekChart;
import com.google.android.material.tabs.TabLayout;

public class WeekChartActivity extends AppCompatActivity {
    private TabLayout tab_layout_new_release_song;
    private ViewPager view_pager_new_release_song;
    private WeekChartViewPageAdapter mViewPagerAdapter;
    private ItemWeekChart itemWeekChart;
    private int position_slider = -1;
    private int week_chart = 0;
    private LinearLayout linear_filter_song;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_chart);


        initView();
        initViewPager();
        onClick();
        getBundleSong();
    }

    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemWeekChart = (ItemWeekChart) bundle.getSerializable("itemWeekChart");
            position_slider = bundle.getInt("position_slide");
            if (itemWeekChart != null && position_slider != -1) {
                int position = -1;
                if (position_slider == 1) {
                    position = 0;
                } else if (position_slider == 2) {
                    position = 2;
                } else {
                    position = 1;
                }
                tab_layout_new_release_song.getTabAt(position).select();
            }
        }
    }

    private void initView() {
        Helper.changeStatusBarColor(WeekChartActivity.this, R.color.black);


        tab_layout_new_release_song = findViewById(R.id.tab_layout_new_release_song);
        view_pager_new_release_song = findViewById(R.id.view_pager_new_release_song);
        linear_filter_song = findViewById(R.id.linear_filter_song);
        img_back = findViewById(R.id.img_back);
    }

    private void initViewPager() {
        mViewPagerAdapter = new WeekChartViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, itemWeekChart, week_chart);
        view_pager_new_release_song.setAdapter(mViewPagerAdapter);

        tab_layout_new_release_song.setupWithViewPager(view_pager_new_release_song);
    }
    private void onClick(){
        linear_filter_song.setOnClickListener(view -> {
            Intent intent = new Intent("send_week_year_to_fragment");
            intent.putExtra("week_chart", "21");
            intent.putExtra("year_chart", "2024");
            LocalBroadcastManager.getInstance(WeekChartActivity.this).sendBroadcast(intent);
        });
        img_back.setOnClickListener(view -> finish());
    }

}
