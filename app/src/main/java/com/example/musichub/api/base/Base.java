package com.example.musichub.api.base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Base {
    private final String version;
    private final String ctime;
    private final String secretKey;
    private final String apiKey;
    private final String baseURL = "https://zingmp3.vn";

    public Base(String apiKey, String secretKey) {
        this.version = "1.10.12"; // Default 0
        this.ctime = String.valueOf(System.currentTimeMillis() / 1000);
        this.secretKey = secretKey != null ? secretKey : "acOrvUS15XRW2o9JksiK1KgQ6Vbds8ZW";
        this.apiKey = apiKey != null ? apiKey : "X5BM3w8N7MKozC0B85o4KMlzLZKhV00y";
    }

    private String createHash256(String params) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(params.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String createHmac512(String str, String key) throws Exception {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        hmacSha512.init(secretKeySpec);
        byte[] hash = hmacSha512.doFinal(str.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    protected String createRequest(String path, Map<String, String> params) {
        // Tạo một bản sao của params để không làm thay đổi params gốc
        Map<String, String> paramMap = new HashMap<>(params);

        // Tạo chuỗi URL bắt đầu với baseURL và path
        StringBuilder urlBuilder = new StringBuilder(baseURL + path + "?");

        // Thêm các giá trị mặc định vào URL

        // Kiểm tra xem params có trống không
        if (!paramMap.isEmpty()) {
            // Thêm các tham số từ map params
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                // Kiểm tra nếu đây là key đầu tiên
                if (entry.equals(paramMap.entrySet().iterator().next())) {
                    urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                } else {
                    urlBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        urlBuilder.append("&ctime=").append(ctime)
                .append("&version=").append(version)
                .append("&apiKey=").append(apiKey);

        return urlBuilder.toString();
    }


    // Create Signature //
    protected String createHashAndHmac(String path, String params) throws NoSuchAlgorithmException, Exception {
        String hash256 = createHash256(params);
        String hmac512 = createHmac512(path + hash256, secretKey);
        return hmac512;
    }

    protected String createHomeSig(String path) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "count=30ctime=" + this.ctime + "page=1version=" + this.version);
    }

    protected String createPodcastSig(String path, String type) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "count=20ctime=" + this.ctime + "page=1type=" + type + "version=" + this.version);
    }

    protected String createSearchSig(String path, String type, int page) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "count=20ctime=" + this.ctime + "page=" + page + "type=" + type + "version=" + this.version);
    }

    protected String createCommentSig(String path, String id) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "count=50ctime=" + this.ctime + "id=" + id + "version=" + this.version);
    }

    protected String createIdSig(String path, String id) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "ctime=" + this.ctime + "id=" + id + "version=" + this.version);
    }

    protected String createNoIdSig(String path) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "ctime=" + this.ctime + "version=" + this.version);
    }

    protected String createSuggestSig(String path) throws NoSuchAlgorithmException, Exception {
        return createHashAndHmac(path, "ctime=" + this.ctime + "language=vinum=10version=" + this.version);
    }
}

