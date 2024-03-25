package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Song extends Base {

    public Song(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    public String getAudio(String songId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/song/get/streaming", songId);
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("id", songId);
            params.put("sig", sig);
            return createRequest("/api/v2/song/get/streaming", params);
        } catch (Exception error) {
            throw error;
        }
    }

    public String getDetail(String songId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/song/get/info", songId);
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("id", songId);
            params.put("sig", sig);
            return createRequest("/api/v2/song/get/info",params);
        } catch (Exception error) {
            throw error;
        }
    }

    public String getLyrics(String songId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/lyric/get/lyric", songId);
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("id", songId);
            params.put("sig", sig);
            return createRequest("/api/v2/lyric/get/lyric", params);
        } catch (Exception error) {
            throw error;
        }
    }
    public String getArtist(String artistId) throws Exception {
        try {
            String sig = createNoIdSig("/api/v2/page/get/artist");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("alias", artistId);
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/artist", params);
        } catch (Exception error) {
            throw error;
        }
    }
    public String getPlaylist(String artistId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/page/get/playlist", artistId);
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("id", artistId);
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/playlist", params);
        } catch (Exception error) {
            throw error;
        }
    }

}
