package com.example.musichub.model.artist;

import com.example.musichub.model.playlist.DataPlaylist;

import java.io.Serializable;
import java.util.ArrayList;

public class SectionArtistPlaylistSingle implements Serializable {
    private String sectionType;
    private String viewType;
    private String title;
    private String link;
    private String sectionId;
    private ArrayList<DataPlaylist> items;
}
