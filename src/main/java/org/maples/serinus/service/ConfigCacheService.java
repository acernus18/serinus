package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.config.ConstConfig;
import org.maples.serinus.model.SerinusConfig;
import org.maples.serinus.repository.SerinusConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class ConfigCacheService {
    private static final Pattern SLAVE_INFO_MATCHER = Pattern.compile("^ip=(.*),port=(\\d+),.*$");
    private static final Pattern SLAVE_SYNC_MATCHER = Pattern.compile("master_sync_in_progress:(\\d)");
    private static final String CACHE_PREFIX = "CONF:SERVICE:";

    private static final String DISTRIBUTED_LOCK = "CONF:SERVICE:LOCK";
    private static final SimpleDateFormat CURRENT_DESC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @AllArgsConstructor
    private enum SLAVE_STATUS {
        SYNC(0),
        SYNC_IN_PROGRESS(1),
        CANNOT_GET_SLAVE_STATUS(2),
        EXCEPTION(3);

        @Getter
        private int value;
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SerinusConfigMapper mapper;

    @Autowired
    private ZKClientService zkClientService;

    @Autowired
    private ConstConfig constConfig;

    @PostConstruct
    public void postConstruct() {
        for (String path : constConfig.getZkSubscribePath()) {
            zkClientService.subscribe(path, x -> log.info("Recv message [{}]", x));
        }
    }

    public Map<String, Integer> fetchSlaveStatus() {

        List<String> urls = listSlaveClientsURL();
        Map<String, Integer> result = new HashMap<>(urls.size());

        for (String url : urls) {
            RedisClient client = RedisClient.create(String.format("redis://%s/", url));
            try (StatefulRedisConnection<String, String> connection = client.connect()) {
                Matcher matcher = SLAVE_SYNC_MATCHER.matcher(connection.sync().info("Replication"));
                if (matcher.find()) {
                    switch (Integer.valueOf(matcher.group(1))) {
                        case 0: {
                            result.put(url, SLAVE_STATUS.SYNC.getValue());
                            break;
                        }

                        case 1: {
                            result.put(url, SLAVE_STATUS.SYNC_IN_PROGRESS.getValue());
                            break;
                        }

                        default: {
                            result.put(url, SLAVE_STATUS.CANNOT_GET_SLAVE_STATUS.getValue());
                            break;
                        }
                    }
                } else {
                    result.put(url, SLAVE_STATUS.CANNOT_GET_SLAVE_STATUS.getValue());
                }
            } catch (Exception e) {
                result.put(url, SLAVE_STATUS.EXCEPTION.getValue());
            }
            client.shutdown();
        }

        return result;
    }

    public JSON flushConcentrationCache(String key) {
        SerinusConfig config = mapper.selectOneByCKey(key);
        JSONObject result = new JSONObject();

        if (config != null && config.getValue() != null) {
            JSON json;
            try {
                config.decode();
                json = config.parseJSONValue();
            } catch (JSONException e) {
                json = new JSONObject();
            }

            redisTemplate.opsForValue().set(CACHE_PREFIX + key, json.toJSONString());

            result.put("CacheKey", CACHE_PREFIX + key);
            result.put("CacheValue", json);
        }
        return result;
    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 60000)
    public void scheduleRefreshCache() {
        log.info("Try Refresh Cache in: {}", getCurrentTimeDescription());

        if (!lock()) {
            return;
        }
        try {
            flushConcentrationCache();
        } catch (Exception e) {
            log.warn("Refresh cache fail, caused by: {}", e.getLocalizedMessage());
        }
        unlock();
    }

    @Async
    public CompletableFuture<Boolean> flushConcentrationCacheAsync() {
        return CompletableFuture.completedFuture(flushConcentrationCache());
    }

    private boolean flushConcentrationCache() {
        List<SerinusConfig> commonConfigs = mapper.selectAll();

        for (SerinusConfig commonConfig : commonConfigs) {
            commonConfig.decode();
            if (commonConfig.getValue() != null) {
                try {
                    String value = "";
                    if (commonConfig.getStatus() == SerinusConfig.STATUS.ONLINE.getValue()) {
                        value = commonConfig.parseJSONValue().toJSONString();
                    }
                    redisTemplate.opsForValue().set(CACHE_PREFIX + commonConfig.getCKey(), value);
                } catch (JSONException e) {
                    log.info("Refreshing Cache Fail: Key={}", CACHE_PREFIX + commonConfig.getCKey());
                }
            }
            log.info("Refreshing Cache: Key={}", CACHE_PREFIX + commonConfig.getCKey());
        }
        return true;
    }

    private List<String> listSlaveClientsURL() {
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();

        if (factory == null) {
            throw new RuntimeException("Cannot get master connection;");
        }

        Properties properties = factory.getConnection().serverCommands().info("Replication");

        if (properties == null) {
            throw new RuntimeException("Cannot get master properties;");
        }

        int slaveNumbers = Integer.valueOf(properties.getProperty("connected_slaves"));
        List<String> redisClientsURL = new ArrayList<>(slaveNumbers);

        for (int i = 0; i < slaveNumbers; i++) {
            String slaveInfo = properties.getProperty(String.format("slave%d", i));

            Matcher matcher = SLAVE_INFO_MATCHER.matcher(slaveInfo);
            if (matcher.find()) {
                String url = String.format("%s:%s", matcher.group(1), matcher.group(2));
                redisClientsURL.add(url);
            }
        }

        return redisClientsURL;
    }

    private boolean lock() {
        log.info("TRY LOCK AT: {}", getCurrentTimeDescription());
        Boolean result = redisTemplate.opsForValue().setIfAbsent(DISTRIBUTED_LOCK, "LOCK KEY");
        if (result != null && result) {
            log.info("LOCK DONE AT: {}", getCurrentTimeDescription());
        } else {
            log.info("LOCK FAIL AT: {}", getCurrentTimeDescription());
        }
        return result != null && result;
    }

    private void unlock() {
        log.info("TRY UNLOCK AT: {}", getCurrentTimeDescription());
        Boolean result = redisTemplate.delete(DISTRIBUTED_LOCK);
        if (result != null && result) {
            log.info("UNLOCK DONE AT: {}", getCurrentTimeDescription());
        } else {
            log.info("UNLOCK FAIL AT: {}", getCurrentTimeDescription());
        }
    }

    private String getCurrentTimeDescription() {
        return CURRENT_DESC.format(Calendar.getInstance().getTime());
    }
}
