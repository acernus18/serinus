package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DistributeLock implements Watcher, AutoCloseable {

    private String lockPath;
    private ZooKeeper keeper;
    private CountDownLatch latch;
    private String node;

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            log.info("Connect success");
        } else {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    public DistributeLock(String address, int sessionTimeout, String path) {
        lockPath = path;
        node = null;

        try {
            keeper = new ZooKeeper(address, sessionTimeout, this);
            Stat stat = keeper.exists(path, false);
            if (stat == null) {
                keeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (KeeperException e) {
            System.out.println(e.getLocalizedMessage() + 1);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage() + 2);
        }
    }

    public boolean lock(long timeout) {
        ArrayList<ACL> accessControl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        CreateMode createMode = CreateMode.EPHEMERAL_SEQUENTIAL;

        try {
            node = keeper.create(lockPath + "/__lock", new byte[0], accessControl, createMode);
            List<String> children = keeper.getChildren(lockPath, false);
            Collections.sort(children);

            if (node.equals(lockPath + "/" + children.get(0))) {
                return true;
            }

            int nodeIndex = Collections.binarySearch(children, node);
            String waitNode = children.get(nodeIndex - 1);

            Stat stat = keeper.exists(lockPath + "/" + waitNode, true);
            if (stat != null) {
                latch = new CountDownLatch(1);
                latch.await(timeout, TimeUnit.MICROSECONDS);
                latch = null;
            }
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unlock() {
        Objects.requireNonNull(node);
        try {
            keeper.delete(node, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        keeper.close();
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch mutex = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {
            try (DistributeLock lock = new DistributeLock("120.78.175.39:30003", 3000, "/lock")) {
                if (lock.lock(1000)) {
                    Thread.sleep(10000);
                    lock.unlock();
                } else {
                    log.info("fail1");
                }
                mutex.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try (DistributeLock lock = new DistributeLock("120.78.175.39:30003", 3000, "/lock")) {
                if (lock.lock(10)) {
                    Thread.sleep(10000);
                    lock.unlock();
                } else {
                    log.info("fail2");
                }
                mutex.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread1.start();


        mutex.await();
        // try (DistributeLock lock = new DistributeLock("120.78.175.39:30003", 3000, "/lock")) {
        //     lock.lock(1000);
        //     Thread.sleep(10000);
        //     lock.unlock();
        //     // mutex.notify();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
}
