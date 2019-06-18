package org.maples.serinus.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class RedisPool {

    private ConcurrentMap<String, StringRedisTemplate> pool = new ConcurrentHashMap<>();

    public void test() {
        RedisConfiguration configuration = new RedisStandaloneConfiguration("120.78.175.39", 30004);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        new RedisTemplate<String, String>();
    }
}
