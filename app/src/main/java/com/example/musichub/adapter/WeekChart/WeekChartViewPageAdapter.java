package com.example.musichub.adapter.WeekChart;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.musichub.fragment.WeekChart.KpopFragment;
import com.example.musichub.fragment.WeekChart.UsUkFragment;
import com.example.musichub.fragment.WeekChart.VnFragment;
import com.example.musichub.model.chart.chart_home.ItemWeekChart;

public class WeekChartViewPageAdapter extends FragmentPagerAdapter {
    private ItemWeekChart itemWeekChart;
    private int weekChartPosition;

    public WeekChartViewPageAdapter(@NonNull FragmentManager fm, int behavior, ItemWeekChart itemWeekChart, int weekChartPosition) {
        super(fm, behavior);
        this.itemWeekChart = itemWeekChart;
        this.weekChartPosition = weekChartPosition;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new VnFragment();
            case 1:
                return new UsUkFragment();
            case 2:
                return new KpopFragment();
            default:
                return new VnFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Việt Nam";
                break;
            case 1:
                title = "US-UK";
                break;
            case 2:
                title = "K-Pop";
                break;
        }
        return title;
    }
}
