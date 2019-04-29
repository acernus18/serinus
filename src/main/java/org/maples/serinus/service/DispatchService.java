package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.utility.SerinusHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DispatchService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier("dispatchScript")
    private DefaultRedisScript<String> dispatchScript;

    public int dispatch(SerinusStrategy strategy, String deviceID) {

        int abSize = 0;
        if (strategy.getType() == 2) {
            abSize = JSON.parseArray(strategy.getContent()).size();
        }

        List<String> keys = Collections.singletonList(strategy.getUuid());

        String argBuilder = deviceID +
                ":" + strategy.getType() +
                ":" + strategy.getPresetType() +
                ":" + abSize +
                ":" + strategy.getMaxCount() +
                ":" + (strategy.getAlwaysReturn() ? 1 : 0);

        String result = redisTemplate.execute(dispatchScript, keys, argBuilder);

        return Integer.valueOf(result != null ? result : "0");
    }
}
