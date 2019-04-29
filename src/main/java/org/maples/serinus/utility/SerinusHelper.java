package org.maples.serinus.utility;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class SerinusHelper {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    public static String base64Encode(String value) {
        return Base64Utils.encodeToString(value.getBytes());
    }

    public static String base64Decode(String value) {
        return new String(Base64Utils.decodeFromString(value));
    }

    private static int compareVersion(String version1, String version2) {
        if (StringUtils.isAnyBlank(version1, version2)) {
            return 0;
        }

        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");

        int minLength = Math.min(versionArray1.length, versionArray2.length);

        for (int i = 0; i < minLength; i++) {
            try {
                int v1 = Integer.valueOf(versionArray1[i]);
                int v2 = Integer.valueOf(versionArray2[i]);

                if (v1 != v2) {
                    return Integer.compare(v1, v2);
                }
            } catch (NumberFormatException e) {
                if (versionArray1[i].compareTo(versionArray2[i]) != 0) {
                    return versionArray1[i].compareTo(versionArray2[i]);
                }
            }
        }

        return versionArray1.length - versionArray2.length;
    }


    public static boolean compare(JSONObject conditionMap, Map<String, String> parameters) {
        for (String key : conditionMap.keySet()) {
            String value = parameters.get(key);
            if (value == null) {
                return false;
            }

            JSONObject condition = conditionMap.getJSONObject(key);
            log.debug("key = {}, value = {}, condition = {}", key, value, condition.toJSONString());
            for (String operator : condition.keySet()) {
                boolean match = false;
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

                log.debug("Operator = {} , match result = {}", operator, match);
                if (!match) {
                    return false;
                }
            }
        }

        return true;
    }

}
