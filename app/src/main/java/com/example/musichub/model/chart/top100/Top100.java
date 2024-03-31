package com.example.musichub.model.chart.top100;


import java.util.ArrayList;

public class Top100 {
    private int err;
    private String msg;
    private ArrayList<DataTop100> data;
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

    public ArrayList<DataTop100> getDataTop100() {
        return data;
    }

    public void setDataTop100(ArrayList<DataTop100> data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
