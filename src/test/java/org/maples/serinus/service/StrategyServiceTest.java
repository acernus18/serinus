package org.maples.serinus.service;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyServiceTest {

    @Autowired
    private StrategyService strategyService;

    @Test
    public void testSave() throws ParseException {
        SerinusStrategy strategy = new SerinusStrategy();

        strategy.setProduct("lispon");
        strategy.setType(0);
        strategy.setTitle("Test");
        strategy.setMaxCount(100);
        strategy.setStartAt(DateUtils.parseDate("2019-10-10 00:00:00", "yy-MM-dd HH:mm:ss"));
        strategy.setEndAt(DateUtils.parseDate("2019-10-12 00:00:00", "yy-MM-dd HH:mm:ss"));
        strategy.setFilter("{'a':'1'}");
        strategy.setContent("{'a':'1'}");
        strategy.setAlwaysReturn(false);
        strategy.setPresetType(0);
        strategy.setOrderInProduct(1);
        strategy.setEnabled(true);

        strategyService.saveStrategy(strategy);

        Assert.assertNotNull(strategy.getUuid());
        SerinusStrategy dbObject = strategyService.getStrategyByUUID(strategy.getUuid());
        dbObject.setContent("{'b':'1'}");
        strategyService.saveStrategy(dbObject);
    }

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