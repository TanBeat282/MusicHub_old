package com.example.musichub.model.chart_home;

import java.io.Serializable;

public class PreviewInfo implements Serializable {
    private int startTime;
    private int endTime;

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
