package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public void subscribe(String path) {
        NodeCache channel = new NodeCache(curatorFramework, path);

        channel.getListenable().addListener(() -> {
            String currentData = new String(channel.getCurrentData().getData());
            log.info("current data = {}", currentData);
        });

        try {
            channel.start();
            nodeCaches.put(path, channel);
        } catch (Exception e) {
            log.error("Subscribe channel for {}, fail for {}", path, e.getLocalizedMessage());
        }
    }
}
