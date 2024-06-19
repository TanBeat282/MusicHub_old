package com.example.musichub.fragment.search_multi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musichub.R;

public class PlaylistAlbumSeachMultiFragment extends Fragment {
    private static final String ARG_DATA = "query";
    private String query;

    public static PlaylistAlbumSeachMultiFragment newInstance(String data) {
        PlaylistAlbumSeachMultiFragment fragment = new PlaylistAlbumSeachMultiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist_album_seach_multi, container, false);
    }
}