package org.maples.serinus.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.component.RouteDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RouteDataSourceTest {

    @Autowired
    private RouteDataSource routeSourceService;

    @Test
    public void test() {
    }
}