package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Search extends Base {
    public Search(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    public String getResult(String q) throws Exception {
        try {
            if (q.trim().isEmpty()) throw new Exception("Invalid query string");
            String sig = createNoIdSig("/api/v2/search/multi");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("q", q);
            params.put("sig", sig);
            return createRequest("/api/v2/search/multi", params);
        } catch (Exception error) {
            throw error;
        }
    }

    public String getResultByType(String q, int type, int page) throws Exception {
        try {
            page = page != 0 ? page : 1;
            type = type != 0 ? type : 1;
            if (q.trim().isEmpty()) throw new Exception("Invalid query string");
            String typeSearch = "";
            HashMap<String, String> params = new LinkedHashMap<>();
            switch (type) {
                case 1:
                    typeSearch = "song";
                    break;
                case 2:
                    typeSearch = "playlist";
                    break;
                case 3:
                    typeSearch = "artist";
                    break;
                case 4:
                    typeSearch = "video";
                    break;
                default:
                    throw new Exception("Invalid type");
            }
            String sig = createSearchSig("/api/v2/search", typeSearch, page);
            params.put("q", q);
            params.put("sig", sig);
            params.put("type", String.valueOf(type));
            params.put("count", "20");
            params.put("page", String.valueOf(page));
            return createRequest("/api/v2/search", params);
        } catch (Exception error) {
            throw error;
        }
    }

    public String getRecommendKeyword() throws NoSuchAlgorithmException, IOException, Exception {
        try {
            String sig = createNoIdSig("/api/v2/app/get/recommend-keyword");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("sig", sig);
            return createRequest("/api/v2/app/get/recommend-keyword", params);
        } catch (Exception error) {
            throw error;
        }
    }

//    public Map<String, String> getSuggestion(String query) throws NoSuchAlgorithmException, IOException, Exception {
//        try {
//            String sig = createNoIdSig("/v1/web/ac-suggestions");
//            String url = "https://ac.zingmp3.vn/v1/web/ac-suggestions?" +
//                    "sig=" + sig +
//                    "&query=" + query +
//                    "&language=vi" +
//                    "&num=10";
//            return fetch(url);
//        } catch (Exception error) {
//            throw error;
//        }
//    }
}
