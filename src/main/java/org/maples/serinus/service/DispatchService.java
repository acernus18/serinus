package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.utility.SerinusHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DispatchService {

    @Autowired
    private StringRedisTemplate redisTemplate;

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

    private boolean withinValidDatetime(Date begin, Date end) {
        Date now = new Date();
        return now.before(end) && now.after(begin);
    }

    public List<JSON> dispatch(List<SerinusStrategy> strategies, Map<String, String> params) {
        List<JSON> resultList = new ArrayList<>();

        String deviceID = params.get("deviceID");
        if (StringUtils.isBlank(deviceID)) {
            return resultList;
        }

        // filter strategies by condition, date, and enabled flag;
        List<SerinusStrategy> filteredStrategies = new ArrayList<>();
        for (SerinusStrategy strategy : strategies) {

            if (!strategy.getEnabled()) {
                log.debug("Drop {}, because of its not enabled.", strategy.getUuid());
                continue;
            }

            if (!withinValidDatetime(strategy.getStartAt(), strategy.getEndAt())) {
                log.debug("Drop {}, because of its not within valid date.", strategy.getUuid());
                continue;
            }

            String condition = strategy.getFilter();
            if (SerinusHelper.compare(JSON.parseObject(condition), params)) {
                filteredStrategies.add(strategy);
            } else {
                log.debug("Drop {}, because of its condition not match.", strategy.getUuid());
            }
        }

        // filter strategies by history, type and white or black list;
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {

            for (SerinusStrategy strategy : filteredStrategies) {
                byte[] key = strategy.getUuid().getBytes();
                byte[] arg = createDispatchArg(strategy, deviceID);
                connection.evalSha(dispatchSHA, ReturnType.VALUE, 1, key, arg);
            }

            return null;
        });

        // parse json object from redis searching result;
        for (int i = 0; i < filteredStrategies.size(); i++) {
            Object result = results.get(i);
            if (result instanceof String) {
                int dispatchResult = Integer.parseInt((String) result);
                SerinusStrategy strategy = filteredStrategies.get(i);

                if (dispatchResult > 0) {

                    if (strategy.getType() != 2) {
                        resultList.add(JSON.parseObject(strategy.getContent()));
                    } else {
                        // for ab type, result represent index of according ab content;
                        JSONArray abArray = JSON.parseArray(strategy.getContent());
                        JSONObject abContent = abArray.getJSONObject(dispatchResult);
                        resultList.add(abContent);
                    }
                } else {
                    log.debug("Drop {}, because of its redis filter.", strategy.getUuid());
                }
            }
        }
        return resultList;
    }
}
