package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoordinateService {

    // private CuratorFramework curatorFramework;
    //
    // private ConcurrentMap<String, NodeCache> nodeCaches;

    // @Autowired
    // public CoordinateService(CuratorFramework curatorFramework) {
    //     this.curatorFramework = curatorFramework;
    //     this.nodeCaches = new ConcurrentHashMap<>();
    // }
    //
    // public void subscribe(String path, Consumer<String> consumer) {
    //     log.info("Subscribing [{}]", path);
    //
    //     try {
    //         if (curatorFramework.checkExists().forPath(path) == null) {
    //             curatorFramework.create().creatingParentsIfNeeded().forPath(path);
    //         }
    //
    //         NodeCache channel = new NodeCache(curatorFramework, path);
    //
    //         channel.getListenable().addListener(() -> {
    //             String currentData = new String(channel.getCurrentData().getData());
    //             consumer.accept(currentData);
    //         });
    //
    //         channel.start();
    //         nodeCaches.put(path, channel);
    //     } catch (Exception e) {
    //         log.error("Subscribe channel for {}, fail for {}", path, e.getLocalizedMessage());
    //     }
    // }
    //
    // public void publish(String path, String message) {
    //
    //     if (!nodeCaches.containsKey(path)) {
    //         log.error("path [{}] does not be subscribed", path);
    //     }
    //
    //     String messageStr = JSON.toJSONString(message);
    //     log.info("Publish message [{}] to [{}]", messageStr, path);
    //
    //     try {
    //         curatorFramework.setData().forPath(path, messageStr.getBytes());
    //     } catch (Exception e) {
    //         log.error("Exception occur in publishing message [{}]", e.getLocalizedMessage());
    //     }
    // }
}
