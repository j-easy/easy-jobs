package org.jeasy.jobs;

import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobDAO;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class AbstractJobDAOTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobDAO jobDAO;

    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void init() throws Exception {
        File file = new File("src/test/resources/easy-jobs-config.json");
        System.setProperty(JobServerConfiguration.CONFIGURATION_PATH_PARAMETER_NAME, file.getAbsolutePath());
    }

    @Before
    public void setUp() throws Exception {
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void testJobPersistence() throws Exception {
        // given
        Job job = new Job(1, "MyJob");

        // when
        jobDAO.save(job);

        // then
        Integer nbJobs = jdbcTemplate.queryForObject("select count(*) from job", Integer.class);
        assertThat(nbJobs).isEqualTo(1);
    }
}
