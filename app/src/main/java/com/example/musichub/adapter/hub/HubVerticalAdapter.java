package com.example.musichub.adapter.hub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.adapter.Playlist.PlaylistAdapter;
import com.example.musichub.adapter.Playlist.PlaylistMoreAdapter;
import com.example.musichub.adapter.Song.SongMoreAdapter;
import com.example.musichub.model.hub.SectionHubPlaylist;
import com.example.musichub.model.hub.SectionHubSong;

import java.util.ArrayList;

public class HubVerticalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SONG = 0;
    private static final int VIEW_TYPE_PLAYLIST = 1;

    private ArrayList<SectionHubPlaylist> sectionHubPlaylists;
    private ArrayList<SectionHubSong> sectionHubSongs;
    private final Context context;
    private final Activity activity;

    public HubVerticalAdapter(Context context, Activity activity, ArrayList<SectionHubSong> sectionHubSongs, ArrayList<SectionHubPlaylist> sectionHubPlaylists) {
        this.context = context;
        this.activity = activity;
        this.sectionHubSongs = sectionHubSongs;
        this.sectionHubPlaylists = sectionHubPlaylists;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<SectionHubSong> sectionHubSongs, ArrayList<SectionHubPlaylist> sectionHubPlaylists) {
        this.sectionHubSongs = sectionHubSongs;
        this.sectionHubPlaylists = sectionHubPlaylists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (!sectionHubSongs.isEmpty() && position < sectionHubSongs.size()) {
            return VIEW_TYPE_SONG;
        } else {
            return VIEW_TYPE_PLAYLIST;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SONG) {
            view = inflater.inflate(R.layout.item_hub_song, parent, false);
            return new SongViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_hub_playlist, parent, false);
            return new PlaylistViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_SONG) {
            SongViewHolder songViewHolder = (SongViewHolder) holder;
            SectionHubSong sectionHubSong = sectionHubSongs.get(position);

            songViewHolder.txt_title_playlist.setText(sectionHubSong.getTitle());

            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4, RecyclerView.HORIZONTAL, false);
            songViewHolder.rv_playlist_horizontal.setLayoutManager(gridLayoutManager);
            SongMoreAdapter songMoreAdapter = new SongMoreAdapter(sectionHubSong.getItems(), 3, activity, context);
            songViewHolder.rv_playlist_horizontal.setAdapter(songMoreAdapter);

            songViewHolder.linear_playlist.setOnClickListener(view -> {
                // Xử lý sự kiện click
            });
        } else {
            int playlistPosition = position - sectionHubSongs.size();
            if (playlistPosition >= 0 && playlistPosition < sectionHubPlaylists.size()) {
                PlaylistViewHolder playlistViewHolder = (PlaylistViewHolder) holder;
                SectionHubPlaylist sectionHubPlaylist = sectionHubPlaylists.get(playlistPosition);

                playlistViewHolder.txt_title_playlist.setText(sectionHubPlaylist.getTitle());

                playlistViewHolder.rv_playlist_horizontal.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                PlaylistMoreAdapter playlistMoreAdapter = new PlaylistMoreAdapter(sectionHubPlaylist.getItems(), activity, context);
                playlistViewHolder.rv_playlist_horizontal.setAdapter(playlistMoreAdapter);

                playlistViewHolder.linear_playlist.setOnClickListener(view -> {
                    // Xử lý sự kiện click
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return sectionHubSongs.size() + sectionHubPlaylists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linear_playlist;
        public TextView txt_title_playlist;
        public RecyclerView rv_playlist_horizontal;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            linear_playlist = itemView.findViewById(R.id.linear_playlist);
            txt_title_playlist = itemView.findViewById(R.id.txt_title_playlist);
            rv_playlist_horizontal = itemView.findViewById(R.id.rv_playlist_horizontal);
        }
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linear_playlist;
        public TextView txt_title_playlist;
        public RecyclerView rv_playlist_horizontal;

        public SongViewHolder(View itemView) {
            super(itemView);
            linear_playlist = itemView.findViewById(R.id.linear_playlist);
            txt_title_playlist = itemView.findViewById(R.id.txt_title_playlist);
            rv_playlist_horizontal = itemView.findViewById(R.id.rv_playlist_horizontal);
        }
    }
}
