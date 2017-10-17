package org.jeasy.jobs;

import org.jeasy.jobs.user.User;
import org.jeasy.jobs.user.UserRepository;
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
        // user admin/admin in db via test-data.sql

        // when
        User user = userRepository.getByNameAndPassword("admin", "21232f297a57a5a743894a0e4a801fc3");

        // then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("21232f297a57a5a743894a0e4a801fc3");
    }

}
