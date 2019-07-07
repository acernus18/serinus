package org.maples.serinus.common;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

public class MyBatisTest {

    @Test
    public void test() {
        DataSource dataSource = DataSourceBuilder.create().build();
        Environment environment = new Environment("", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addInterceptor(null);

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = builder.build(configuration);

        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("");
            session.insert("");
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
    }
}
