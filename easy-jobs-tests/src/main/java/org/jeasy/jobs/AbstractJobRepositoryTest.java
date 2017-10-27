package org.jeasy.jobs;

import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class AbstractJobRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobRepository jobRepository;

    private JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void init() throws Exception {
        File file = new File("src/test/resources/database.properties");
        System.setProperty(DataSourceConfiguration.DATA_SOURCE_CONFIGURATION_PROPERTY, file.getAbsolutePath());
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
        Job job = new Job(1, "MyJob", "my job");

        // when
        jobRepository.save(job);

        // then
        Integer nbJobs = jdbcTemplate.queryForObject("select count(*) from ej_job", Integer.class);
        assertThat(nbJobs).isEqualTo(1);
    }

    public void testFindAllJobs() throws Exception {
        // given
        Job job1 = new Job(1, "MyFirstJob", "my first job");
        Job job2 = new Job(2, "MySecondJob", "my second job");
        jobRepository.save(job1);
        jobRepository.save(job2);

        // when
        List<Job> jobs = jobRepository.findAll();

        // then
        assertThat(jobs).isNotEmpty().hasSize(2);
    }

    public void testFindByJobId() throws Exception {
        // given
        Job job = new Job(1, "MyJob", "my job");
        jobRepository.save(job);

        // when
        Job actual = jobRepository.findById(1);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getName()).isEqualTo("MyJob");
        assertThat(actual.getDescription()).isEqualTo("my job");
    }
}
