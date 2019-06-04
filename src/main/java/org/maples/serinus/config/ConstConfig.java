package org.maples.serinus.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Getter
@Configuration
@PropertySource("classpath:properties/const.properties")
public class ConstConfig {

    @Value("${zk.subscribe.path}")
    private String[] zkSubscribePath;

    @Value("${slave.datasource}")
    private String[] slaveDatasource;
}
