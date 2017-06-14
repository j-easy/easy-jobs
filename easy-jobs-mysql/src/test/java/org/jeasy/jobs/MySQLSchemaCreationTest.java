package org.jeasy.jobs;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.util.Properties;

import static org.junit.Assert.fail;

public class MySQLSchemaCreationTest {

    @Test
    public void canExecuteSQLScriptAgainstMySQLDatabase() throws Exception {
        // Given
        Properties properties = new Properties();
        properties.load(MySQLSchemaCreationTest.class.getResourceAsStream("/database.properties"));
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(properties.getProperty("easy.jobs.mysql.url"));
        mysqlDataSource.setUser(properties.getProperty("easy.jobs.mysql.user"));
        mysqlDataSource.setPassword(properties.getProperty("easy.jobs.mysql.password"));

        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);

        // When
        try {
            databasePopulator.execute(mysqlDataSource);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to run sql script against mysql database");
        }

        // then
        // expecting no exception or test failure
    }

}