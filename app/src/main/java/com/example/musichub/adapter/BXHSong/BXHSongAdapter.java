package com.example.musichub.adapter.BXHSong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.example.musichub.bottomsheet.BottomSheetOptionSong;
import com.example.musichub.model.chart.chart_home.Items;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class BXHSongAdapter extends RecyclerView.Adapter<BXHSongAdapter.ViewHolder> {
    private ArrayList<Items> songList;
    private final Context context;
    private final Activity activity;
    private int selectedPosition = -1;
    private int positionRank = 0;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Items> fillterList) {
        this.songList = fillterList;
        notifyDataSetChanged();
    }

    public BXHSongAdapter(ArrayList<Items> songList, Activity activity, Context context) {
        this.songList = songList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bxh_song, parent, false);
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

        if (song.getRakingStatus() > 0) {
            setImageAndTint(holder.img_bxh, holder.txt_number_top_song, holder.txt_rank_status, R.drawable.ic_trending_up, R.color.green, song.getRakingStatus());
        } else if (song.getRakingStatus() < 0) {
            setImageAndTint(holder.img_bxh, holder.txt_number_top_song, holder.txt_rank_status, R.drawable.ic_trending_down, R.color.red, song.getRakingStatus());
        } else {
            setImageAndTint(holder.img_bxh, holder.txt_number_top_song, holder.txt_rank_status, R.drawable.ic_trending_flat, R.color.colorSecondaryText, song.getRakingStatus());
        }


        if (selectedPosition == position) {
            int colorSpotify = ContextCompat.getColor(context, R.color.colorSpotify);
            holder.nameTextView.setTextColor(colorSpotify);
            holder.aniPlay.setVisibility(View.VISIBLE);
        } else {
            holder.nameTextView.setTextColor(Color.WHITE);
            holder.aniPlay.setVisibility(View.GONE);
        }
        int premiumColor;
        if (song.getStreamingStatus() == 2) {
            premiumColor = ContextCompat.getColor(context, R.color.yellow);
        } else {
            premiumColor = ContextCompat.getColor(context, R.color.white);
        }
        holder.nameTextView.setTextColor(premiumColor);

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetInfo(song);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showBottomSheetInfo(song);
                return false;
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (song.getStreamingStatus() == 2) {
                Toast.makeText(context, "Không thể phát bài hát Premium!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, PlayNowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("song", song);
                bundle.putInt("position_song", position);
                bundle.putSerializable("song_list", songList);
                bundle.putInt("title_now_playing", 0);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
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
        public TextView txt_number_top_song;
        public TextView txt_rank_status;
        public ImageView img_bxh;
        public LottieAnimationView aniPlay;
        public ImageView btn_more;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            artistTextView = itemView.findViewById(R.id.artistTextView);

            txt_number_top_song = itemView.findViewById(R.id.txt_number_top_song);
            img_bxh = itemView.findViewById(R.id.img_bxh);
            txt_rank_status = itemView.findViewById(R.id.txt_rank_status);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            aniPlay = itemView.findViewById(R.id.aniPlay);
            btn_more = itemView.findViewById(R.id.btn_more);
            artistTextView.setSelected(true);
            nameTextView.setSelected(true);
        }
    }

    private void setImageAndTint(ImageView imageView, TextView textView, TextView txt_rank_status, int resId, int colorId, int rankingStatus) {
        imageView.setImageResource(resId);
        int color = ContextCompat.getColor(context, colorId);
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        textView.setText(String.valueOf(positionRank++));

        txt_rank_status.setText(String.valueOf(rankingStatus));
        txt_rank_status.setTextColor(color);
        if (rankingStatus != 0) {
            txt_rank_status.setVisibility(View.VISIBLE);
        } else {
            txt_rank_status.setVisibility(View.GONE);
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

    private void showBottomSheetInfo(Items items) {
        BottomSheetOptionSong bottomSheetOptionSong = new BottomSheetOptionSong(context, activity, items);
        bottomSheetOptionSong.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetOptionSong.getTag());
    }


}
