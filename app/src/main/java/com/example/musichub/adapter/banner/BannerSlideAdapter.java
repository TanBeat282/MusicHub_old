package com.example.musichub.adapter.banner;

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
import com.example.musichub.activity.ViewPlaylistActivity;
import com.example.musichub.activity.WeekChartActivity;
import com.example.musichub.model.chart.home.home_new.banner.HomeDataItemBannerItem;
import com.example.musichub.model.chart.home.home_new.week_chart.HomeDataItemWeekChartItem;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class BannerSlideAdapter extends RecyclerView.Adapter<BannerSlideAdapter.ViewHolder> {
    private ArrayList<HomeDataItemBannerItem> homeDataItemBannerItems;
    private ViewPager2 viewPager2;
    private final Context context;
    private final Activity activity;


    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<HomeDataItemBannerItem> homeDataItemBannerItems) {
        this.homeDataItemBannerItems = homeDataItemBannerItems;
        notifyDataSetChanged();
    }

    public BannerSlideAdapter(ArrayList<HomeDataItemBannerItem> homeDataItemBannerItems, ViewPager2 viewPager2, Context context, Activity activity) {
        this.homeDataItemBannerItems = homeDataItemBannerItems;
        this.viewPager2 = viewPager2;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeDataItemBannerItem homeDataItemBannerItem = homeDataItemBannerItems.get(position);

        Glide.with(context)
                .load(homeDataItemBannerItem.getBanner())
                .into(holder.roundedImageView);

        if (position == homeDataItemBannerItems.size() -2){
            viewPager2.post(runnable);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("encodeId", homeDataItemBannerItem.getEncodeId());
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return homeDataItemBannerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView roundedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.roundedImageView);
        }
    }

    private final Runnable runnable = new Runnable() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            homeDataItemBannerItems.addAll(homeDataItemBannerItems);
            notifyDataSetChanged();
        }
    };

}
