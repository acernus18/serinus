package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.utility.SerinusHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DispatchService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${script.dispatch.sha}")
    private String dispatchSHA;

    @PostConstruct
    public void postConstruct() {
        if (StringUtils.isBlank(dispatchSHA)) {
            dispatchSHA = redisTemplate.execute((RedisCallback<String>) connection -> {
                String script = null;
                try {
                    URL dispatchScriptURL = ResourceUtils.getURL("classpath:schema/dispatch.lua");
                    script = IOUtils.toString(dispatchScriptURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (StringUtils.isBlank(script)) {
                    throw new RuntimeException("Load Dispatch Script Fail");
                }
                return connection.scriptLoad(script.getBytes());
            });
            log.info("Load finished, sha = {}", dispatchSHA);
        }
    }

    private byte[] createDispatchArg(SerinusStrategy strategy, String deviceID) {

        int abSize = 0;
        if (strategy.getType() == 2) {
            abSize = JSON.parseArray(strategy.getContent()).size();
        }

        String args = deviceID +
                ":" + strategy.getType() +
                ":" + strategy.getPresetType() +
                ":" + abSize +
                ":" + strategy.getMaxCount() +
                ":" + (strategy.getAlwaysReturn() ? 1 : 0);

        return args.getBytes();
    }

    public Map<String, Integer> dispatch(List<SerinusStrategy> strategies, Map<String, String> params) {
        Map<String, Integer> resultsMap = new HashMap<>();

        String deviceID = params.get("deviceID");
        if (StringUtils.isBlank(deviceID)) {
            return resultsMap;
        }

        List<SerinusStrategy> filteredStrategies = new ArrayList<>();
        for (SerinusStrategy strategy : strategies) {
            String condition = strategy.getFilter();
            if (SerinusHelper.compare(JSON.parseObject(condition), params)) {
                filteredStrategies.add(strategy);
            }
        }

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {

            for (SerinusStrategy strategy : filteredStrategies) {
                byte[] key = strategy.getUuid().getBytes();
                byte[] arg = createDispatchArg(strategy, deviceID);
                connection.evalSha(dispatchSHA, ReturnType.VALUE, 1, key, arg);
            }

            return null;
        });

        for (int i = 0; i < filteredStrategies.size(); i++) {
            Object result = results.get(i);
            if (result instanceof String) {
                resultsMap.put(filteredStrategies.get(i).getUuid(), Integer.valueOf((String) result));
            }
        }
        return resultsMap;
    }
}
