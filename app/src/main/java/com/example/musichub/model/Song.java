package com.example.musichub.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    private String thumb;
    private String thumb_medium;
    private String artist;
    private String id;
    private String name;
    private String lyric;
    private String link_audio;
    private String code;
    private Artist mArtist;

    public Song() {
    }

    public Song(String thumb, String thumb_medium, String artist, String id, String name, String lyric, String link_audio, String code, Artist mArtist) {
        this.thumb = thumb;
        this.thumb_medium = thumb_medium;
        this.artist = artist;
        this.id = id;
        this.name = name;
        this.lyric = lyric;
        this.link_audio = link_audio;
        this.code = code;
        this.mArtist = mArtist;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getThumb_medium() {
        return thumb_medium;
    }

    public void setThumb_medium(String thumb_medium) {
        this.thumb_medium = thumb_medium;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getLink_audio() {
        return link_audio;
    }

    public void setLink_audio(String link_audio) {
        this.link_audio = link_audio;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Artist getmArtist() {
        return mArtist;
    }

    public void setmArtist(Artist mArtist) {
        this.mArtist = mArtist;
    }
}
