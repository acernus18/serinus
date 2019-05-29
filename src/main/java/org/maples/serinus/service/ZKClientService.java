package org.maples.serinus.service;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ZKClientService {

    private ThreadPoolTaskExecutor listeners;

    @Autowired
    private CuratorFramework curatorFramework;

    @PostConstruct
    public void postConstruct() {
        this.listeners = new ThreadPoolTaskExecutor();
    }

    public void test() {
        listeners.execute(() -> {});
    }
}
