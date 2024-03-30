package com.example.musichub.model.chart_home.Home;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemHome {
    private String sectionType;
    private String viewType;
    private String title;
    private String link;
    private String sectionId;
    @SerializedName("items")
    private ArrayList<ItemSilder> itemSilderArrayList;
    private ArrayList<ItemsData> items;

}
