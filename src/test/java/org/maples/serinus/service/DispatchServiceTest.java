package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DispatchServiceTest {

    @Autowired
    private DispatchService dispatchService;

    @Test
    public void dispatch() {

        SerinusStrategy strategy = new SerinusStrategy();

        strategy.setUuid("popped:test");
        strategy.setType(0);
        strategy.setPresetType(0);
        strategy.setMaxCount(2);
        strategy.setAlwaysReturn(false);

        String result = dispatchService.dispatch(strategy, "maples");

        log.info("result = {}", result);
    }
}