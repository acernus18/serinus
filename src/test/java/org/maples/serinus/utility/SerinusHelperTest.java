package org.maples.serinus.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SerinusHelperTest {
    @Test
    public void compare() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("app_ver", "15.6");
        parameters.put("flag", "1");
        String condition1 = "{\"app_ver\": {\"$GTE\": \"10.7.2\"}, \"flag\": {\"$IN\": [\"1\", \"2\"]}}";
        String condition2 = "{\"app_ver\": {\"$GTE\": \"10.7.2\", \"$LT\": \"12.0.1\"}}";
        SerinusHelper.compare(JSON.parseObject(condition1), parameters);
        SerinusHelper.compare(JSON.parseObject(condition2), parameters);

        // eval "return redis.call('sismember', KEYS[1], ARGV[1])" 1 bwlist:8c0bc051572b4d11b4df59a8 maples_black
        // eval "return redis.call('sismember', KEYS[1], ARGV[1])" 1 bwlist:8c0bc051572b4d11b4df59a8 maples_black
    }
}