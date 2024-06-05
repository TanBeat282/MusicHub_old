package com.example.musichub.adapter.Playlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewPlaylistActivity;
import com.example.musichub.model.playlist.DataPlaylist;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<DataPlaylist> dataPlaylistArrayList;
    private final Context context;
    private final Activity activity;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<DataPlaylist> fillterList) {
        this.dataPlaylistArrayList = fillterList;
        notifyDataSetChanged();
    }

    public PlaylistAdapter(ArrayList<DataPlaylist> dataPlaylistArrayList, Activity activity, Context context) {
        this.dataPlaylistArrayList = dataPlaylistArrayList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DataPlaylist dataPlaylist = dataPlaylistArrayList.get(position);

        holder.txt_name.setText(dataPlaylist.getTitle());
        holder.txt_follow.setText(String.valueOf(dataPlaylist.getReleaseDate()));
        Glide.with(context)
                .load(dataPlaylist.getThumbnail())
                .into(holder.img_avatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewPlaylistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist", dataPlaylist);
            intent.putExtras(bundle);

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return dataPlaylistArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView img_avatar;
        public TextView txt_name;
        public TextView txt_follow;

        public ViewHolder(View itemView) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_follow = itemView.findViewById(R.id.txt_follow);
            txt_name.setSelected(true);
            txt_follow.setSelected(true);
        }
    }

}
