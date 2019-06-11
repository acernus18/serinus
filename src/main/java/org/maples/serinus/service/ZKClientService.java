package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.maples.serinus.model.ZKMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

@Slf4j
@Service
public class ZKClientService {

    private ConcurrentMap<String, NodeCache> nodeCaches;

    @Autowired
    private CuratorFramework curatorFramework;

    @PostConstruct
    public void postConstruct() {
        nodeCaches = new ConcurrentHashMap<>();
    }

    public void subscribe(String path, Consumer<String> consumer) {
        log.info("Subscribing [{}]", path);
        NodeCache channel = new NodeCache(curatorFramework, path);

        channel.getListenable().addListener(() -> {
            String currentData = new String(channel.getCurrentData().getData());
            consumer.accept(currentData);
        });

        try {
            if (curatorFramework.checkExists().forPath(path) == null) {
                curatorFramework.create().creatingParentsIfNeeded().forPath(path);
            }
            channel.start();
            nodeCaches.put(path, channel);
        } catch (Exception e) {
            log.error("Subscribe channel for {}, fail for {}", path, e.getLocalizedMessage());
        }
    }

    public void publish(String path, ZKMessage message) {

        if (!nodeCaches.containsKey(path)) {
            log.error("path [{}] does not be subscribed", path);
        }

        String messageStr = JSON.toJSONString(message);
        log.info("Publish message [{}] to [{}]", messageStr, path);

        try {
            curatorFramework.setData().forPath(path, messageStr.getBytes());
        } catch (Exception e) {
            log.error("Exception occur in publishing message [{}]", e.getLocalizedMessage());
        }
    }

    public Map<String, String> getCurrentChannelData() {
        Map<String, String> result = new HashMap<>();
        nodeCaches.forEach((k, v) -> result.put(k, new String(v.getCurrentData().getData())));
        return result;
    }

    public void distributeLock() {
        InterProcessLock lock = new InterProcessMutex(curatorFramework, "/123");
    }
}
