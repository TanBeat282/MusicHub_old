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

        // Kiểm tra xem thời gian phát hiện tại có nằm trong khoảng thời gian của dòng lyric không
        if (isCurrentLyric(position)) {
            // Nếu có, thay đổi màu sắc của item thành màu colorAccent
            holder.textViewContent.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            // Nếu không, sử dụng màu sắc mặc định cho item
            holder.textViewContent.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText2));
        }

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

    private boolean isCurrentLyric(int position) {
        if (position < lyricLines.size() - 1) {
            LyricLine currentLine = lyricLines.get(position);
            LyricLine nextLine = lyricLines.get(position + 1);
            return currentPlaybackTime >= currentLine.getStartTime() && currentPlaybackTime < nextLine.getStartTime();
        } else {
            // Trường hợp này xảy ra khi position là dòng lyric cuối cùng
            LyricLine currentLine = lyricLines.get(position);
            return currentPlaybackTime >= currentLine.getStartTime();
        }
    }
}

