package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testDate() throws ParseException {
        Date begin = DateUtils.parseDate("2019/10/10 10:10:10", "yyyy/MM/dd HH:mm:ss");
        Date now1 = DateUtils.parseDate("2019/10/10 10:10:11", "yyyy/MM/dd HH:mm:ss");
        Date end = DateUtils.parseDate("2019/10/10 10:10:12", "yyyy/MM/dd HH:mm:ss");
        Date now2 = DateUtils.parseDate("2019/10/10 10:10:13", "yyyy/MM/dd HH:mm:ss");
        // log.info("Result {} = {}", now1, dispatchService.withinValidDatetime(now1, begin, end));
        // log.info("Result {} = {}", now2, dispatchService.withinValidDatetime(now2, begin, end));
    }
}