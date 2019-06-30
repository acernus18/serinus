package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.maples.serinus.model.SerinusStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class StrategyCacheService {

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private ConcurrentMap<String, List<SerinusStrategy>> cacheMap = new ConcurrentHashMap<>();

    private List<SerinusStrategy> createResults(List<SerinusStrategy> values) {
        List<SerinusStrategy> results = new ArrayList<>(values.size());
        for (SerinusStrategy value : values) {
            SerinusStrategy temp = new SerinusStrategy();
            try {
                BeanUtils.copyProperties(temp, value);
                results.add(temp);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.warn(e.getLocalizedMessage());
            }
        }
        return results;
    }

    public List<SerinusStrategy> listStrategiesByProduct(String product) {

        if (cacheMap.containsKey(product)) {
            return createResults(cacheMap.get(product));
        }

        Boolean hasCacheKey = redisTemplate.hasKey(product);
        if (hasCacheKey != null && hasCacheKey) {
            Long size = redisTemplate.boundListOps(product).size();

            if (size == null) {
                throw new NullPointerException();
            }

            List<SerinusStrategy> results = new ArrayList<>();
            for (long i = 0; i < size; i++) {
                String strategy = redisTemplate.boundListOps(product).index(i);
                results.add(JSON.parseObject(strategy, SerinusStrategy.class));
            }

            cacheMap.put(product, results);
        } else {
            List<SerinusStrategy> dbObjects = strategyService.getSerinusStrategyList().get(product);
            for (SerinusStrategy dbObject : dbObjects) {
                redisTemplate.boundListOps(product).leftPush(JSON.toJSONString(dbObject));
            }

            cacheMap.put(product, dbObjects);
        }

        return createResults(cacheMap.get(product));
    }
}
