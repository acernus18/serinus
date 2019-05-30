package org.maples.serinus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor commonPool() {
        return new ThreadPoolTaskExecutor();
    }

}
