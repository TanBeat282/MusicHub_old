package com.example.musichub.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.bottomsheet.BottomSheetOptionSong;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.chart.chart_home.Items;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder> {
    private ArrayList<Artists> artistsArrayList;
    private final Context context;
    private final Activity activity;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Artists> fillterList) {
        this.artistsArrayList = fillterList;
        notifyDataSetChanged();
    }

    public ArtistsAdapter(ArrayList<Artists> artistsArrayList, Activity activity, Context context) {
        this.artistsArrayList = artistsArrayList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artists, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Artists artist = artistsArrayList.get(position);


        holder.txt_name.setText(artist.getName());
        holder.txt_follow.setText(Helper.convertToIntString(artist.getTotalFollow()) + " quan tâm");
        Glide.with(context)
                .load(artist.getThumbnail())
                .into(holder.img_avatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewArtistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("artist", artist);
                intent.putExtras(bundle);

                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return artistsArrayList.size();
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
