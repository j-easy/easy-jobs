package org.jeasy.jobs;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.io.File;

import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {org.jeasy.jobs.ContextConfiguration.class})
public class H2SchemaCreationTest {

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        File file = new File("src/test/resources/easy-jobs-config.json");
        System.setProperty(JobServerConfiguration.CONFIGURATION_PATH_PARAMETER_NAME, file.getAbsolutePath());
    }

    @Test
    public void canExecuteSQLScriptAgainstH2Database() throws Exception {
        // Given
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);

        // When
        try {
            databasePopulator.execute(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to run sql script against h2 database");
        }

        // then
        // expecting no exception or test failure
    }

}