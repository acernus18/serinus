package org.maples.serinus.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class SyncTest {

    @Test
    public void test() throws InterruptedException {
        Object resource = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (resource) {
                log.debug("1 get lock");

                try {
                    resource.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                log.debug("1 release lock");
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resource) {
                log.debug("2 get lock");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                log.debug("2  release lock");
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
