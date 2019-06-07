package org.maples.serinus.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.maples.serinus.component.RouteDataSource;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Slf4j
@Aspect
@Configuration
public class DataSourceConfig {
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    public @interface Slave {

    }

    @Around("@annotation(org.maples.serinus.config.DataSourceConfig.Slave)")
    public Object pointCut(ProceedingJoinPoint point) throws Throwable {

        Object result;
        try {
            RouteDataSource.setRead();
            result = point.proceed();
        } finally {
            RouteDataSource.setWrite();
        }

        return result;
    }
}
