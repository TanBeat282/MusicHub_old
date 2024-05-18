package com.example.musichub.adapter.Top100Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewPlaylistActivity;
import com.example.musichub.model.playlist.DataPlaylist;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class Top100MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_BUTTON = 1;

    private ArrayList<DataPlaylist> itemsTop100s;
    private final Context context;

    public Top100MoreAdapter(ArrayList<DataPlaylist> itemsTop100s, Context context) {
        this.itemsTop100s = itemsTop100s;
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<DataPlaylist> fillterList) {
        this.itemsTop100s = fillterList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top100, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_all, parent, false);
            return new ButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            DataPlaylist itemsTop100 = itemsTop100s.get(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.nameTextView.setText(itemsTop100.getTitle());
            Glide.with(context)
                    .load(itemsTop100.getThumbnailM())
                    .into(itemViewHolder.thumbImageView);
            itemViewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewPlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("playlist", itemsTop100);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        } else {
            ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;
            buttonViewHolder.img_more_all.setOnClickListener(v -> {
                //
            });
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(itemsTop100s.size(), 6); // Chỉ hiển thị tối đa 6 item
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 5) {
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_BUTTON;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView thumbImageView;
        public TextView nameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_more_all;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            img_more_all = itemView.findViewById(R.id.thumbImageView);
        }
    }
}
