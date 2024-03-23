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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.activity.PlayNowVideoActivity;
import com.example.musichub.activity.VideoActivity;
import com.example.musichub.model.Song;
import com.example.musichub.service.MyService;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private ArrayList<Song> songList;
    private final Context context;
    private int selectedPosition = -1;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Song> fillterList) {
        this.songList = fillterList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPosition(ArrayList<Song> fillterList, int position) {
        this.songList = fillterList;
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public VideoAdapter(ArrayList<Song> songList, Context context) {
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
        Song song = songList.get(position);

        holder.nameTextView.setText(song.getName());
        holder.artistTextView.setText(song.getArtist());
        Glide.with(context)
                .load(song.getThumb_medium())
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
            Intent intent = new Intent(context, PlayNowVideoActivity.class);
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

}
