package org.maples.serinus.component;

import org.maples.serinus.utility.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;

@Component
public class RouteDataSource extends AbstractRoutingDataSource {

    @Autowired
    private DataSourceProperties primaryProperties;

    @PostConstruct
    public void postConstruct() {
        DataSource primary = primaryProperties.initializeDataSourceBuilder().build();
        this.setDefaultTargetDataSource(primary);

        HashMap<Object, Object> dataSources = new HashMap<>();

        dataSources.put(DataSourceHelper.READ, primary);
        dataSources.put(DataSourceHelper.WRITE, primary);
        this.setTargetDataSources(dataSources);
    }


    @Override
    protected Object determineCurrentLookupKey() {
        String typeKey = DataSourceHelper.getReadOrWrite();

        if (typeKey == null) {
            return DataSourceHelper.WRITE;
        }

        if (typeKey.equals(DataSourceHelper.WRITE)) {
            return DataSourceHelper.WRITE;
        }

        return DataSourceHelper.READ;
    }
}
