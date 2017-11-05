package org.jeasy.jobs.user;

import org.jeasy.jobs.ContextConfiguration;
import org.jeasy.jobs.DataSourceConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class AbstractUserRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserRepository userRepository;

    @BeforeClass
    public static void init() throws Exception {
        File file = new File("src/test/resources/database.properties");
        System.setProperty(DataSourceConfiguration.DATA_SOURCE_CONFIGURATION_PROPERTY, file.getAbsolutePath());
    }

    @Before
    public void setUp() throws Exception {
        Resource schema = new ClassPathResource("database-schema.sql");
        Resource data = new ClassPathResource("test-data.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(schema, data);
        databasePopulator.execute(dataSource);
    }

    public void testGetUserByNameAndPassword() throws Exception {
        // given
        // user root/root in db via test-data.sql

        // when
        User user = userRepository.getByNameAndPassword("root", "63a9f0ea7bb98050796b649e85481845");

        // then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("root");
        assertThat(user.getPassword()).isEqualTo("63a9f0ea7bb98050796b649e85481845");
    }

}
