package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceHelper {

    public static final String READ = "READ";
    public static final String WRITE = "WRITE";
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

    public static String getReadOrWrite() {
        return dataSource.get();
    }

    public static void clear() {
        dataSource.remove();
    }
}
