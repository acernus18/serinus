package org.maples.serinus.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ParseUtils {

    private static final String DEFAULT_SECTION = "INI::Utility::default";
    private static final boolean SUPPORT_SECTION = false;

    private static String getTrim(String line) {
        return line.substring(0, line.indexOf('[')).trim();
    }

    private static Map<String, List<String>> parseSection(String[] value) {
        String sectionName = DEFAULT_SECTION;

        Map<String, List<String>> sections = new HashMap<>();
        sections.put(sectionName, new ArrayList<>());

        for (String lineRaw : value) {
            String line = lineRaw.trim();

            if (StringUtils.isBlank(line) || line.startsWith(";") || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]") && SUPPORT_SECTION) {
                sectionName = line.substring(1, line.length() - 1);
                sections.put(sectionName, new ArrayList<>());
            } else {
                if (line.contains("=")) {
                    sections.get(sectionName).add(line);
                }
            }
        }

        return sections;
    }


    private static String extractString(String str) {
        if (Pattern.matches("^\".*\"$", str) || Pattern.matches("^'.*'$", str)) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private static JSON postHandle(JSONObject object) {
        JSONArray array = new JSONArray();

        for (int i = 0; i < object.size(); i++) {
            if (object.containsKey(String.valueOf(i))) {
                array.add(object.get(String.valueOf(i)));
            }
        }

        return array.size() == object.size() ? array : object;
    }

    public static JSON parseINIString(String value) {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        String[] lines = value.split("\n");

        Map<String, List<String>> sections = parseSection(lines);
        JSONObject result = new JSONObject();

        for (String section : sections.keySet()) {
            List<String> sectionLines = sections.get(section);

            Map<String, JSONArray> indexType = new HashMap<>();
            Map<String, JSONObject> objectType = new HashMap<>();
            Map<String, String> keyValueType = new HashMap<>();

            for (String line : sectionLines) {
                String v = line.substring(line.indexOf('=') + 1).trim();
                v = extractString(v);
                if (Pattern.matches("^\".*\"$", v)) {
                    v = v.substring(1, v.length() - 1);
                }

                if (Pattern.matches("^.+\\[]\\s*=.*$", line)) {
                    // a[]=1
                    String arrayName = extractString(getTrim(line));

                    indexType.putIfAbsent(arrayName, new JSONArray());
                    indexType.get(arrayName).add(v);
                } else if (Pattern.matches("^.+\\[.+]\\s*=.*$", line)) {
                    // a[string]=1
                    String arrayName = extractString(getTrim(line));
                    String key = extractString(line.substring(line.indexOf('[') + 1, line.indexOf(']')));

                    objectType.putIfAbsent(arrayName, new JSONObject());
                    objectType.get(arrayName).put(key, v);
                } else if (Pattern.matches("^.+=.*$", line)) {
                    String key = extractString(line.substring(0, line.indexOf('=')).trim());
                    keyValueType.put(key, v);
                }
            }

            if (section.equals(DEFAULT_SECTION)) {
                result.putAll(indexType);
                result.putAll(objectType);
                result.putAll(keyValueType);
            } else {
                JSONObject content = new JSONObject();
                content.putAll(indexType);
                content.putAll(objectType);
                content.putAll(keyValueType);
                result.put(section, content);
            }
        }

        return postHandle(result);
    }

    public static JSON parseJSONString(String value) {

        String content = value.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            return JSONArray.parseArray(value);
        }

        return JSON.parseObject(value);
    }
}

