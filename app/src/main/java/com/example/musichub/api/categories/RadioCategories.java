package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.util.Map;


public class RadioCategories extends Base {

    public RadioCategories(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    public Map<String, String> getUserActiveRadio(String ids) throws Exception {
        try {
            String sig = createNoIdSig("/api/v2/livestream/get/active-user");
            Map<String, String> params = createRequest();
            params.put("ids", ids);
            params.put("sig", sig);
            return params;
        } catch (Exception error) {
            throw error;
        }
    }
}
