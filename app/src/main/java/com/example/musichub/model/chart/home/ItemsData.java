package com.example.musichub.model.chart.home;

import com.example.musichub.model.chart.chart_home.Items;

import java.util.ArrayList;

public class ItemsData {
    ArrayList<Items> all;
    ArrayList<Items> vPop;
    ArrayList<Items> others;

    public ArrayList<Items> getAll() {
        return all;
    }

    public void setAll(ArrayList<Items> all) {
        this.all = all;
    }

    public ArrayList<Items> getvPop() {
        return vPop;
    }

    public void setvPop(ArrayList<Items> vPop) {
        this.vPop = vPop;
    }

    public ArrayList<Items> getOthers() {
        return others;
    }

    public void setOthers(ArrayList<Items> others) {
        this.others = others;
    }
}
