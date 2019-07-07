package org.maples.serinus.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class ZookeeperTest {

    @Test
    public void test() throws IOException {
        ZooKeeper keeper = new ZooKeeper("", 10, event -> log.debug(event.getPath()));
    }
}
