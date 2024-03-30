package com.example.musichub.model.chart_home.Home;

import com.example.musichub.model.chart_home.Data;

import java.io.Serializable;

public class Home implements Serializable {
    private int err;
    private String msg;
    private DataHome data;
    private long timestamp;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }
//
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataHome getData() {
        return data;
    }

    public void setData(DataHome data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
