package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.util.Map;


public class HubCategories extends Base {

    public HubCategories() {
    }

    public Map<String, String> getHub(String id) throws Exception {
        try {
            String sig = createIdSig("/api/v2/page/get/hub-detail", id);
            Map<String, String> params = createRequest();
            params.put("id", id);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }
}
