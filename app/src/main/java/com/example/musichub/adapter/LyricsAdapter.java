package com.example.musichub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.model.LyricLine;
import com.example.musichub.service.MyService;

import java.util.List;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder> {

    private final Context context;
    private List<LyricLine> lyricLines;
    private long currentPlaybackTime;

    public LyricsAdapter(Context context, List<LyricLine> lyricLines) {
        this.context = context;
        this.lyricLines = lyricLines;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLyricLines(List<LyricLine> lyricLines) {
        this.lyricLines = lyricLines;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentPlaybackTime(long currentPlaybackTime) {
        this.currentPlaybackTime = currentPlaybackTime;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LyricsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_lyrics, parent, false);
        return new LyricsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LyricsViewHolder holder, int position) {
        LyricLine lyricLine = lyricLines.get(position);
        holder.textViewContent.setText(lyricLine.getContent());

        // Đổi màu cho item nếu đúng thời gian bài hát đang hát
        int colorSpotify;// Thay đổi màu sắc thành màu colorAccent
        if (currentPlaybackTime >= lyricLine.getStartTime() - 700) {
            colorSpotify = ContextCompat.getColor(context, R.color.white);
        } else {
            colorSpotify = ContextCompat.getColor(context, R.color.colorSecondaryText2);
        }
        holder.textViewContent.setTextColor(colorSpotify); // Thay đổi màu sắc thành màu colorAccent

        holder.textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MyService.class);
                intent.putExtra("seek_to_position", (int) lyricLine.getStartTime());
                context.startService(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lyricLines.size();
    }

    static class LyricsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContent;

        public LyricsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }
}

