package com.example.musichub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.model.Song;
import com.example.musichub.model.chart_home.Items;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class TopSongAdapter extends RecyclerView.Adapter<TopSongAdapter.ViewHolder> {
    private ArrayList<Items> songList;
    private final Context context;
    private int selectedPosition = -1;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Items> fillterList) {
        this.songList = fillterList;
        notifyDataSetChanged();
    }
    public TopSongAdapter(ArrayList<Items> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Items song = songList.get(position);

        holder.nameTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtistsNames());
        Glide.with(context)
                .load(song.getThumbnail())
                .into(holder.thumbImageView);

        if (selectedPosition == position) {
            int colorSpotify = ContextCompat.getColor(context, R.color.colorSpotify);
            holder.nameTextView.setTextColor(colorSpotify);
            holder.aniPlay.setVisibility(View.VISIBLE);
        } else {
            holder.nameTextView.setTextColor(Color.WHITE);
            holder.aniPlay.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayNowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("song", song);
            bundle.putInt("position_song", position);
            bundle.putSerializable("song_list", songList);
            bundle.putInt("title_now_playing", 0);
            intent.putExtras(bundle);

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView thumbImageView;
        public TextView artistTextView;
        public TextView nameTextView;
        public LottieAnimationView aniPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            aniPlay = itemView.findViewById(R.id.aniPlay);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updatePlayingStatus(String currentPlayingEncodeId) {
        if (songList != null) {
            for (int i = 0; i < songList.size(); i++) {
                Items item = songList.get(i);
                if (item.getEncodeId().equals(currentPlayingEncodeId)) {
                    selectedPosition = i;
                    notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi để cập nhật giao diện
                    return;
                }
            }
        }
        selectedPosition = -1; // Nếu không tìm thấy, không bài hát nào được chọn
        notifyDataSetChanged();
    }


}
