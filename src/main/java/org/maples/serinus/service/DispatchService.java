package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.utility.SerinusHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DispatchService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier("dispatchScript")
    private RedisScript<String> dispatchScript;

    public String dispatch(SerinusStrategy strategy, String deviceID) {

        int abSize = 0;
        if (strategy.getType() == 2) {
            abSize = JSON.parseArray(strategy.getContent()).size();
        }

        List<String> keys = Collections.singletonList(strategy.getUuid());

        String args = deviceID +
                ":" + strategy.getType() +
                ":" + strategy.getPresetType() +
                ":" + abSize +
                ":" + strategy.getMaxCount() +
                ":" + (strategy.getAlwaysReturn() ? 1 : 0);

        return redisTemplate.execute(dispatchScript, keys, args);
    }

    public List<SerinusStrategy> filter(List<SerinusStrategy> strategies, Map<String, String> params) {
        List<SerinusStrategy> result = new ArrayList<>();

        for (SerinusStrategy strategy : strategies) {
            String condition = strategy.getFilter();
            if (SerinusHelper.compare(JSON.parseObject(condition), params)) {
                result.add(strategy);
            }
        }

        return result;
    }
}
