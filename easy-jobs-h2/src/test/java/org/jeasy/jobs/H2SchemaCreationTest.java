package org.jeasy.jobs;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.util.Properties;

import static org.junit.Assert.fail;

public class H2SchemaCreationTest {

    @Test
    public void canExecuteSQLScriptAgainstH2Database() throws Exception {
        // Given
        Properties properties = new Properties();
        properties.load(H2SchemaCreationTest.class.getResourceAsStream("/database.properties"));
        JdbcDataSource H2DataSource = new JdbcDataSource();
        H2DataSource.setUrl(properties.getProperty("easy.jobs.h2.url"));
        H2DataSource.setUser(properties.getProperty("easy.jobs.h2.user"));
        H2DataSource.setPassword(properties.getProperty("easy.jobs.h2.password"));

        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);

        // When
        try {
            databasePopulator.execute(H2DataSource);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to run sql script against h2 database");
        }

        // then
        // expecting no exception or test failure
    }

}