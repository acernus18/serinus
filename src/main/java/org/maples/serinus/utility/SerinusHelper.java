package org.maples.serinus.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.UUID;

public class SerinusHelper {
    private static int compareVersion(String v1, String v2) {
        if (StringUtils.isAnyBlank(v1, v2)) {
            return 0;
        }

        String[] versionArray1 = v1.split("\\.");
        String[] versionArray2 = v2.split("\\.");

        int minLength = Math.min(versionArray1.length, versionArray2.length);

        for (int i = 0; i < minLength; i++) {
            if (versionArray1[i].compareTo(versionArray2[i]) != 0) {
                return versionArray1[i].compareTo(versionArray2[i]);
            }
        }

        return versionArray1.length - versionArray2.length;
    }


    public static boolean compare(JSONObject conditionMap, Map<String, String> parameters) {
        // {
        //  "app_version":
        //  {
        //    "in": ["123", "13"],
        //    "in": ["123", "13"],
        //    "in": ["123", "13"],
        //  }
        // }
        for (String conditionKey : conditionMap.keySet()) {
            String value = parameters.get(conditionKey);
            if (value == null) {
                return false;
            }

            //  {
            //    "in": ["123", "13"],
            //    "in": ["123", "13"],
            //    "in": ["123", "13"],
            //  }
            JSONObject condition = conditionMap.getJSONObject(conditionKey);
            for (String operator : condition.keySet()) {
                boolean match = false;
                // "in": ["123", "13"],
                switch (operator) {
                    case "$IN":
                        match = condition.getJSONArray(operator).contains(value);
                        break;
                    case "$N_IN":
                        match = !condition.getJSONArray(operator).contains(value);
                        break;
                    case "$EQ":
                        match = condition.getString(operator).equalsIgnoreCase(value);
                        break;
                    case "$N_EQ":
                        match = !condition.getString(operator).equalsIgnoreCase(value);
                        break;
                    case "$REGEX":
                        match = value.matches(condition.getString(operator));
                        break;
                    case "$N_REGEX":
                        match = !value.matches(condition.getString(operator));
                        break;
                    case "$GT":
                        match = compareVersion(value, condition.getString(operator)) > 0;
                        break;
                    case "$GTE":
                        match = compareVersion(value, condition.getString(operator)) >= 0;
                        break;
                    case "$LT":
                        match = compareVersion(value, condition.getString(operator)) < 0;
                        break;
                    case "$LTE":
                        match = compareVersion(value, condition.getString(operator)) <= 0;
                        break;
                }

                if (!match) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
}
