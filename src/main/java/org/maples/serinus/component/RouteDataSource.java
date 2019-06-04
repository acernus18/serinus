package org.maples.serinus.component;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.config.ConstConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Component
public class RouteDataSource extends AbstractRoutingDataSource {
    private static final String READ = "READ";
    private static final String WRITE = "WRITE";
    private static final String JDBC_URL = "jdbc:mysql://%s:%s/db_serinus_system?useSSL=false";
    private static final ThreadLocal<String> dataSource = new ThreadLocal<>();

    public static ThreadLocal<String> getDataSource() {
        return dataSource;
    }

    public static void setRead() {
        dataSource.set(READ);
        log.info("Set read");
    }

    public static void setWrite() {
        dataSource.set(WRITE);
        log.info("Set write");
    }

    public static void clear() {
        dataSource.remove();
    }

    @Autowired
    private DataSourceProperties primaryProperties;

    @Autowired
    private ConstConfig constConfig;

    @PostConstruct
    public void postConstruct() {
        DataSource primary = primaryProperties.initializeDataSourceBuilder().build();
        this.setDefaultTargetDataSource(primary);

        HashMap<Object, Object> dataSources = new HashMap<>();

        dataSources.put(WRITE, primary);
        dataSources.put(READ, primary);

        for (String slave : constConfig.getSlaveDatasource()) {
            String[] config = slave.split(":");

            DataSource dataSource = DataSourceBuilder.create()
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .username(config[0])
                    .password(config[1])
                    .url(String.format(JDBC_URL, config[2], config[3])).build();

            dataSources.put(READ, dataSource);
        }

        this.setTargetDataSources(dataSources);
    }


    @Override
    protected Object determineCurrentLookupKey() {
        String typeKey = getDataSource().get();

        if (typeKey == null) {
            return WRITE;
        }

        if (typeKey.equals(WRITE)) {
            return WRITE;
        }

        return READ;
    }
}
