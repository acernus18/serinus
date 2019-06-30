package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@CacheConfig(cacheNames = "CommonCache")
public class CacheService {
    private static final long REDIS_CACHE_TIME = 10L;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String serialize(Object object) {
        return JSON.toJSONString(object);
    }

    private <T> T deserialize(String object, Class<T> type) {
        return JSON.parseObject(object, type);
    }

    @Cacheable(key = "#cacheKey")
    public <T> T get(String cacheKey, Class<T> type, Supplier<T> supplier) {
        T result;

        log.info("Miss cache, get {} value from redis", cacheKey);
        Boolean hasKey = redisTemplate.hasKey(cacheKey);
        if (hasKey != null && hasKey) {
            result = deserialize(redisTemplate.opsForValue().get(cacheKey), type);
        } else {
            log.info("Miss Redis cache, get {} value from supplier", cacheKey);
            result = supplier.get();
            redisTemplate.opsForValue().set(cacheKey, serialize(result), REDIS_CACHE_TIME, TimeUnit.MICROSECONDS);
        }

        return result;
    }

    @CacheEvict(key = "#cacheKey")
    public void evict(String cacheKey) {
        redisTemplate.delete(cacheKey);
    }
}
