package com.example.musichub.model.search;

import com.example.musichub.model.artist.DataArtist;

import java.io.Serializable;

public class Search implements Serializable {
    private int err;
    private String msg;
    private DataSearch data;
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

    public DataSearch getData() {
        return data;
    }

    public void setData(DataSearch data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
