package com.example.musichub.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musichub.R;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.model.Song;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;

public class RelatedSongFragment extends Fragment {
    private RecyclerView recyclerView;
    private TopSongAdapter adapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private ArrayList<Song> songArrayList;
    private int positionSong = -1;
    public RelatedSongFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_related_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
    }
}