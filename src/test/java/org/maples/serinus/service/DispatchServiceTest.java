package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        strategy.setMaxCount(10);
        strategy.setAlwaysReturn(true);

        Map<String, String> params = new HashMap<>();
        params.put("deviceID", "123maples");

        // Map<String, Integer> resultMap = dispatchService.dispatch(Collections.singletonList(strategy), params);
        // log.info(JSON.toJSONString(resultMap, true));
    }
}