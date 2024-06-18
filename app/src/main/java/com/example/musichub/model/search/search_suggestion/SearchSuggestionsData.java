package com.example.musichub.model.search.search_suggestion;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchSuggestionsData implements Serializable {
    private String tracking;
    private ArrayList<SearchSuggestionsDataItem> items;

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public ArrayList<SearchSuggestionsDataItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<SearchSuggestionsDataItem> items) {
        this.items = items;
    }
}
