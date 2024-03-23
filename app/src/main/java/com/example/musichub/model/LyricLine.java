package com.example.musichub.model;

public class LyricLine {
    private long startTime;
    private String content;

    public LyricLine() {
    }

    public LyricLine(long startTime, String content) {
        this.startTime = startTime;
        this.content = content;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
