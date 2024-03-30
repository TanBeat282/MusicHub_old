package com.example.musichub.model.playlist;

import com.example.musichub.model.chart_home.Data;

public class Playlist {
    private int err;
    private String msg;
    private DataPlaylist data;
    private long timestamp;

    public DataPlaylist getData() {
        return data;
    }

    public void setData(DataPlaylist data) {
        this.data = data;
    }

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
