package org.jeasy.jobs;

import org.junit.Before;
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
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class SchemaCreationTest {

    @Autowired
    private DataSource dataSource;

    @Before
    public void init() throws Exception {
        File file = new File("src/test/resources/database.properties");
        System.setProperty(DataSourceConfiguration.DATA_SOURCE_CONFIGURATION_PROPERTY, file.getAbsolutePath());
    }

    public void canExecuteSQLScript() throws Exception {
        // Given
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);

        // When
        try {
            databasePopulator.execute(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to run sql script against selected database");
        }

        // then
        // expecting no exception or test failure
    }

}