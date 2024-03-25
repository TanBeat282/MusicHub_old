package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Video extends Base {
    public Video(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    public String getRelatedVideos(String videoId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/video/get/section-relate", videoId);
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("videoId", videoId);
            params.put("sig", sig);
            return createRequest("/api/v2/video/get/section-relate", params);
        } catch (Exception error) {
            throw error;
        }
    }

    public String getDetail(String videoId) throws Exception {
        try {
            String sig = createIdSig("/api/v2/page/get/video", videoId);
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("videoId", videoId);
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/video", params);
        } catch (Exception error) {
            throw error;
        }
    }
}
