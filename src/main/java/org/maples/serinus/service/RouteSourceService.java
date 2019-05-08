package org.maples.serinus.service;

import org.maples.serinus.utility.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RoutingSourceService extends AbstractRoutingDataSource {

    private AtomicInteger count = new AtomicInteger(0);

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void postConstruct() {
        System.out.println();
    }


    // @Override
    protected Object determineCurrentLookupKey() {
        String typeKey = DataSourceHelper.getReadOrWrite();

        if (typeKey == null) {
            throw new NullPointerException("数据库路由时，决定使用哪个数据库源类型不能为空...");
        }

        if (typeKey.equals(DataSourceHelper.WRITE)) {
            return DataSourceHelper.WRITE;
        }

        int number = count.getAndAdd(1);
        int lookupKey = number % 10;
        return DataSourceHelper.READ + (lookupKey + 1);
    }
}
