package com.example.musichub.model.artist;

import com.example.musichub.model.chart.chart_home.Items;

import java.io.Serializable;
import java.util.ArrayList;

public class SectionArtistSong implements Serializable {
    private String sectionType;
    private String viewType;
    private String title;
    private String link;
    private String sectionId;
    private ArrayList<Items> items;
    private Items topAlbum;

}
