package org.maples.serinus.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.maples.serinus.utility.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
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

    // @Bean
    // @Primary
    // public DataSource primaryDatasource() {
    //     return DataSourceBuilder.create().build();
    // }

    // @Bean(name = "roundRobinDataSourceProxy")
    // public AbstractRoutingDataSource roundRobinDataSourceProxy() {
    //
    //     Map<Object, Object> targetDataSources = new HashMap<>();
    //     //把所有数据库都放在targetDataSources中,注意key值要和determineCurrentLookupKey()中代码写的一至，否则切换数据源时找不到正确的数据源
    //     targetDataSources.put(DataSourceType.write.getType(), writeDataSource);
    //     targetDataSources.put(DataSourceType.read.getType() + "1", readDataSource01);
    //     targetDataSources.put(DataSourceType.read.getType() + "2", readDataSource02);
    //
    //     final int readSize = Integer.parseInt(readDataSourceSize);
    //     //     MyAbstractRoutingDataSource proxy = new MyAbstractRoutingDataSource(readSize);
    //
    //     //路由类，寻找对应的数据源
    //
    //
    //     proxy.setDefaultTargetDataSource(writeDataSource);//默认库
    //     proxy.setTargetDataSources(targetDataSources);
    //     return proxy;
    // }

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
            DataSourceHelper.setRead();
            result = point.proceed();
        } finally {
            DataSourceHelper.setWrite();
        }

        return result;
    }
}
