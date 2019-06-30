package org.maples.serinus.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class SerinusStrategyMapperTest {

    @Autowired
    private SerinusStrategyMapper strategyMapper;

    @Test
    public void selectCountByProduct() {
        log.info("LISPON count = {}", strategyMapper.selectCountByProduct("lispon"));
        List<SerinusStrategy> strategies = strategyMapper.selectAllEnabledByProduct("lispon");

        log.info("Result list = {}", strategies);
    }
}