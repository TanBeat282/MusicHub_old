package com.example.musichub.model.chart_home.NewRelease;
;

public class NewRelease {
    private int err;
    private String msg;
    private DataNewRelease data;
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

    public DataNewRelease getData() {
        return data;
    }

    public void setData(DataNewRelease data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
