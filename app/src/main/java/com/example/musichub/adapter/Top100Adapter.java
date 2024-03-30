package com.example.musichub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.model.chart_home.Items;
import com.example.musichub.model.chart_home.Top100.DataTop100;
import com.example.musichub.model.chart_home.Top100.ItemsTop100;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class Top100Adapter extends RecyclerView.Adapter<Top100Adapter.ViewHolder> {
    private ArrayList<ItemsTop100> itemsTop100s;
    private final Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<ItemsTop100> fillterList) {
        this.itemsTop100s = fillterList;
        notifyDataSetChanged();
    }
    public Top100Adapter(ArrayList<ItemsTop100> itemsTop100s, Context context) {
        this.itemsTop100s = itemsTop100s;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top100, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemsTop100 itemsTop100 = itemsTop100s.get(position);

        holder.nameTextView.setText(itemsTop100.getTitle());
        Glide.with(context)
                .load(itemsTop100.getThumbnailM())
                .into(holder.thumbImageView);
    }


    @Override
    public int getItemCount() {
        return itemsTop100s.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView thumbImageView;
        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }


}
