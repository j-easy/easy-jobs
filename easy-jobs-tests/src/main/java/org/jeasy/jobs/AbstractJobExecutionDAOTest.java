package org.jeasy.jobs;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionDAO;
import org.jeasy.jobs.execution.JobExecutionStatus;
import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobDAO;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestDAO;
import org.jeasy.jobs.request.JobRequestStatus;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class AbstractJobExecutionDAOTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private JobRequestDAO jobRequestDAO;
    @Autowired
    private JobExecutionDAO jobExecutionDAO;

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

    public void testJobExecutionPersistence() throws Exception {
        // given
        jobDAO.save(new Job(1, "MyJob"));
        jobRequestDAO.save(new JobRequest(1, "", JobRequestStatus.PENDING, LocalDateTime.now(), null));

        // when
        jobExecutionDAO.save(new JobExecution(1, JobExecutionStatus.RUNNING, null, LocalDateTime.now(), null));

        // then
        Integer nbJobExecutions = jdbcTemplate.queryForObject("select count(*) from job_execution", Integer.class);
        assertThat(nbJobExecutions).isEqualTo(1);
    }

    public void testJobExecutionUpdate() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plus(2, ChronoUnit.MINUTES);
        jobDAO.save(new Job(1, "MyJob"));
        jobRequestDAO.save(new JobRequest(1, "", JobRequestStatus.PENDING, now, null));
        jobExecutionDAO.save(new JobExecution(1, JobExecutionStatus.RUNNING, null, now, null));

        // when
        jobExecutionDAO.update(1, JobExitStatus.SUCCEEDED, endDate);

        // then
        JobExecution jobExecution = jobExecutionDAO.getByJobRequestId(1);
        assertThat(jobExecution.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.FINISHED);
        assertThat(jobExecution.getJobExitStatus()).isEqualTo(JobExitStatus.SUCCEEDED);
        assertThat(jobExecution.getEndDate()).isEqualToIgnoringSeconds(endDate); // sometimes this test fails when ignoring only nanoseconds
    }
}
