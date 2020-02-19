package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Service
public class WatchService implements Watcher {

    private static final String MESSAGE = "/message";

    private ZooKeeper keeper;

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeDataChanged:
                String path = event.getPath();
                subscribe(path);
                break;
        }
    }

    @PostConstruct
    public void postConstruct() {
        try {
            keeper = new ZooKeeper("120.78.175.39:30003", 3000, this);
            subscribe(MESSAGE);
        } catch (IOException e) {
            log.warn(e.getLocalizedMessage());
        }
    }

    private void subscribe(String path) {
        try {
            Stat stat = keeper.exists(path, false);
            if (stat == null) {
                keeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            byte[] data = keeper.getData(path, true, stat);
            log.info("Recv: {}", new String(data));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
