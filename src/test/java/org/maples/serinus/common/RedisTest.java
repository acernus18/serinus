package org.maples.serinus.common;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
public class RedisTest {
    @Test
    public void testRedisClient() {
        RedisClient redisClient = RedisClient.create("redis://120.78.175.39:30004");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> sync = connection.sync();
        log.debug(sync.info());
        log.debug(sync.get("maples_test_key"));
        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void testRedisTemplate() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory("120.78.175.39", 30004);
        factory.afterPropertiesSet();
        RedisTemplate<String, String> redisClient = new RedisTemplate<>();
        redisClient.setConnectionFactory(factory);

        // use stringSerializer to avoid weird code like '\xac\xed\x00\x05t\x00\x0f';
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisClient.setKeySerializer(stringSerializer);
        redisClient.setValueSerializer(stringSerializer);
        redisClient.setHashKeySerializer(stringSerializer);
        redisClient.setHashValueSerializer(stringSerializer);

        redisClient.afterPropertiesSet();

        log.debug(redisClient.opsForValue().get("maples_test_key"));
    }
}
