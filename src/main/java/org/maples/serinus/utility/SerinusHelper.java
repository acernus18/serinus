package org.maples.serinus.utility;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.UUID;

public class SerinusHelper {
    public static boolean compare(JSONObject conditions, Map<String, String> parameters) {

        for (String conditionKey : conditions.keySet()) {
            
        }

        return false;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
}
