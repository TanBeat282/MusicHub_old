package com.example.musichub.api.categories;

import com.example.musichub.api.base.Base;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Chart extends Base {
    public Chart(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    // home
    public String getChartHome() throws NoSuchAlgorithmException, IOException, Exception {
        try {
            String sig = createNoIdSig("/api/v2/page/get/chart-home");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/chart-home", params);
        } catch (Exception error) {
            throw error;
        }
    }

    public String getHome(int page) throws NoSuchAlgorithmException, IOException, Exception {
        if (page != 0) {
            page = 1;
        } else {
            page = page;
        }
        try {
            String sig = createNoIdSig("/api/v2/page/get/home");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("page", String.valueOf(page));
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/home", params);
        } catch (Exception error) {
            throw error;
        }
    }

    // top 100 us ur vn
    public String getTop100() throws NoSuchAlgorithmException, IOException, Exception {
        try {
            String sig = createNoIdSig("/api/v2/page/get/top-100");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/top-100", params);
        } catch (Exception error) {
            throw error;
        }
    }


    // nhac moi phat hanh
    public String getNewReleaseChart() throws NoSuchAlgorithmException, IOException, Exception {
        try {
            String sig = createNoIdSig("/api/v2/page/get/newrelease-chart");
            HashMap<String, String> params = new LinkedHashMap<>();
            params.put("sig", sig);
            return createRequest("/api/v2/page/get/newrelease-chart", params);
        } catch (Exception error) {
            throw error;
        }
    }

//    public String getWeekChart(String idGenreIds,int nation, String a) throws NoSuchAlgorithmException, IOException, Exception {
//
//        try {
//            Map<String, String> nationKeyCode = new HashMap<>();
//            switch (nation) {
//                case 1:
//                    nationKeyCode.put("vn", "IWZ9Z08I");
//                    break;
//                case 2:
//                    nationKeyCode.put("kr", "IWZ9Z0BO");
//                    break;
//                case 3:
//                    nationKeyCode.put("us", "IWZ9Z0BW");
//                    break;
//                default:
//                    throw new Exception("Invalid type");
//            }
//
//            String sig = createNoIdSig("/api/v2/page/get/week-chart");
//            HashMap<String, String> params = new LinkedHashMap<>();
//            params.put("id", idGenreIds);
//            params.put("sig", sig);
//            return createRequest("/api/v2/page/get/week-chart", params);
//        } catch (Exception error) {
//            throw error;
//        }
//    }


//    public String getWeekly(int type) throws NoSuchAlgorithmException, IOException, Exception {
//        try {
//            WeeklyType defaultParams = new WeeklyType("vn", 0, 0);
//            String nation = params.nation != null ? params.nation : defaultParams.nation;
//            int week = params.week != null ? params.week : defaultParams.week;
//            int year = params.year != null ? params.year : defaultParams.year;
//
//            Map<String, String> nationKeyCode = new HashMap<>();
//            nationKeyCode.put("vn", "IWZ9Z08I");
//            nationKeyCode.put("kr", "IWZ9Z0BO");
//            nationKeyCode.put("us", "IWZ9Z0BW");
//
//            String sig = createIdSig("/api/v2/page/get/week-chart", nationKeyCode.get(nation));
//            return createRequest("/api/v2/page/get/week-chart", Map.of("id", nationKeyCode.get(nation), "year", String.valueOf(year), "week", String.valueOf(week), "sig", sig));
//        } catch (Exception error) {
//            throw error;
//        }
//    }
//
}
