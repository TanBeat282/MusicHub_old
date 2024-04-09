package com.example.musichub.model.chart.home;

import java.io.Serializable;

public class Home implements Serializable {
    private int err;
    private String msg;
    private SectionHome hSlider;
    private SectionHome hRecent;
    private SectionHome newRelease;
    private long timestamp;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SectionHome gethSlider() {
        return hSlider;
    }

    public void sethSlider(SectionHome hSlider) {
        this.hSlider = hSlider;
    }

    public SectionHome gethRecent() {
        return hRecent;
    }

    public void sethRecent(SectionHome hRecent) {
        this.hRecent = hRecent;
    }

    public SectionHome getNewRelease() {
        return newRelease;
    }

    public void setNewRelease(SectionHome newRelease) {
        this.newRelease = newRelease;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
