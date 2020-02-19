package org.maples.serinus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// @EnableCaching
@SpringBootApplication
@EnableTransactionManagement
public class SerinusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SerinusApplication.class, args);
    }

}
