package com.example.musichub.model.chart.home;

import java.util.ArrayList;

public class DataHome {
//    private ArrayList<ItemHome> items;
    private boolean hasMore;
    private int total;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
