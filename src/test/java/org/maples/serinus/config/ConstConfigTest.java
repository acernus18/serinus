package org.maples.serinus.config;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ConstConfigTest {

    @Autowired
    private ConstConfig constConfig;

    @Test
    public void test() {
        for (String s : constConfig.getZkSubscribePath()) {
            log.info(s);
        }
    }
}