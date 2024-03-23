package com.example.musichub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.model.Song;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<Song> songList;
    private final Context context;
    private SharedPreferences sharedPreferences;

    @SuppressLint("NotifyDataSetChanged")
    public void setFillterList(ArrayList<Song> fillterList) {
        this.songList = fillterList;
        notifyDataSetChanged();
    }

    public SearchAdapter(ArrayList<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Song song = songList.get(position);

        holder.artistTextView.setText(song.getArtist());
        holder.nameTextView.setText(song.getName());

        Glide.with(context)
                .load(song.getThumb_medium())
                .into(holder.thumbImageView);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, PlayNowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("song", song);
            bundle.putInt("position_song", position);
            bundle.putSerializable("song_list", songList);
            bundle.putInt("title_now_playing", 1);
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

        public ViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
