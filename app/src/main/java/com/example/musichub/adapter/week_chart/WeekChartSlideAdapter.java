package com.example.musichub.adapter.week_chart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.WeekChartActivity;
import com.example.musichub.model.chart.home.home_new.week_chart.HomeDataItemWeekChartItem;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class WeekChartSlideAdapter extends RecyclerView.Adapter<WeekChartSlideAdapter.ViewHolder> {
    private ArrayList<HomeDataItemWeekChartItem> homeDataItemWeekChartItems;
    private ViewPager2 viewPager2;
    private final Context context;
    private final Activity activity;


    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<HomeDataItemWeekChartItem> homeDataItemWeekChartItems) {
        this.homeDataItemWeekChartItems = homeDataItemWeekChartItems;
        notifyDataSetChanged();
    }

    public WeekChartSlideAdapter(ArrayList<HomeDataItemWeekChartItem> homeDataItemWeekChartItems, ViewPager2 viewPager2, Context context, Activity activity) {
        this.homeDataItemWeekChartItems = homeDataItemWeekChartItems;
        this.viewPager2 = viewPager2;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_chart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeDataItemWeekChartItem homeDataItemWeekChartItem = homeDataItemWeekChartItems.get(position);

        Glide.with(context)
                .load(homeDataItemWeekChartItem.getCover())
                .into(holder.roundedImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WeekChartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("itemWeekChart", homeDataItemWeekChartItem);
                // 0 us-uk 1 vn 2 k-pop
                bundle.putInt("position_slide", position);
                intent.putExtras(bundle);

                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return homeDataItemWeekChartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView roundedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.roundedImageView);
        }
    }

}
