package com.example.musichub.adapter.Album;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewPlaylistActivity;
import com.example.musichub.bottomsheet.BottomSheetOptionSong;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Album;
import com.example.musichub.model.chart.chart_home.Items;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class AlbumNewReleaseAdapter extends RecyclerView.Adapter<AlbumNewReleaseAdapter.ViewHolder> {
    private ArrayList<Album> albumArrayList;
    private final Context context;
    private final Activity activity;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Album> filterList) {
        this.albumArrayList = filterList;
        notifyDataSetChanged();
    }


    public AlbumNewReleaseAdapter(ArrayList<Album> albumArrayList, Activity activity, Context context) {
        this.albumArrayList = albumArrayList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Album album = albumArrayList.get(position);


        holder.nameTextView.setText(album.getTitle());
        holder.artistTextView.setText(album.getArtistsNames());
        Glide.with(context)
                .load(album.getThumbnail())
                .into(holder.thumbImageView);
        holder.txt_time_release_date.setText(Helper.convertLongToTime(album.getReleasedAt()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("playlist", album);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView thumbImageView;
        public TextView artistTextView;
        public TextView nameTextView;
        public TextView txt_time_release_date;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            txt_time_release_date = itemView.findViewById(R.id.txt_time_release_date);
            artistTextView.setSelected(true);
            nameTextView.setSelected(true);
        }
    }

    private void showBottomSheetInfo(Items items) {
        BottomSheetOptionSong bottomSheetOptionSong = new BottomSheetOptionSong(context, activity, items);
        bottomSheetOptionSong.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetOptionSong.getTag());
    }


}
