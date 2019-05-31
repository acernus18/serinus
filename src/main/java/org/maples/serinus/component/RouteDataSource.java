package org.maples.serinus.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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

    @PostConstruct
    public void postConstruct() {
        DataSource primary = primaryProperties.initializeDataSourceBuilder().build();
        this.setDefaultTargetDataSource(primary);

        HashMap<Object, Object> dataSources = new HashMap<>();

        dataSources.put(READ, primary);
        dataSources.put(WRITE, primary);
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
