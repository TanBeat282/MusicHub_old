package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.util.Map;


public class SongCategories extends Base {

    public SongCategories(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    public Map<String, String> getAudio(String songId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/song/get/streaming", songId);
            Map<String, String> params = createRequest();
            params.put("id", songId);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }

    public Map<String, String> getDetail(String songId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/song/get/info", songId);
            Map<String, String> params = createRequest();
            params.put("id", songId);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }

    public Map<String, String> getLyrics(String songId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/lyric/get/lyric", songId);
            Map<String, String> params = createRequest();
            params.put("id", songId);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }

    public Map<String, String> getArtist(String artistId) throws Exception {
        try {
            String sig = createNoIdSig("/api/v2/page/get/artist");
            Map<String, String> params = createRequest();
            params.put("alias", artistId);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }

    public Map<String, String> getPlaylist(String artistId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/page/get/playlist", artistId);
            Map<String, String> params = createRequest();
            params.put("id", artistId);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }
    public Map<String, String> getAlbum(String artistId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/page/get/album", artistId);
            Map<String, String> params = createRequest();
            params.put("id", artistId);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }

}
