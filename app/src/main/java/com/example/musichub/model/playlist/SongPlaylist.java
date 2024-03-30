package com.example.musichub.model.playlist;

import com.example.musichub.model.chart_home.Items;

import java.util.ArrayList;

public class SongPlaylist {
    private ArrayList<Items> items;
    private int total;
    private int totalDuration;

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
}
