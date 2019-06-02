package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZKClientServiceTest {

    @Autowired
    private ZKClientService zkClientService;

    @Test
    public void subscribe() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        lock.lock();
        zkClientService.subscribe("/hello/info", log::info);
        condition.await();
    }

    @Test
    public void subscribe2() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        zkClientService.subscribe("/hello/info", log::info);
        latch.await();
    }
}