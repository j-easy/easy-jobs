package org.jeasy.jobs.request;

import org.jeasy.jobs.ContextConfiguration;
import org.jeasy.jobs.DataSourceConfiguration;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.jobs.request.JobExecutionRequest.newJobExecutionRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class AbstractJobExecutionRequestRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobExecutionRequestRepository jobExecutionRequestRepository;

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

    public void testJobExecutionRequestPersistence() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob", "my job"));

        // when
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("").withStatus(JobExecutionRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));

        // then
        Integer nbJobExecutionRequests = jdbcTemplate.queryForObject("select count(*) from ej_job_execution_request", Integer.class);
        assertThat(nbJobExecutionRequests).isEqualTo(1);
    }

    public void testFindJobExecutionRequestsByStatus() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob", "my job"));
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("").withStatus(JobExecutionRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("").withStatus(JobExecutionRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("").withStatus(JobExecutionRequestStatus.SUBMITTED).withCreationDate(LocalDateTime.now()));
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("").withStatus(JobExecutionRequestStatus.PROCESSED).withCreationDate(LocalDateTime.now()).withProcessingDate(LocalDateTime.now().plus(2, ChronoUnit.MINUTES)));

        // when
        List<JobExecutionRequest> pendingJobExecutionRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PENDING);
        List<JobExecutionRequest> submittedJobExecutionRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.SUBMITTED);
        List<JobExecutionRequest> processedJobExecutionRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PROCESSED);

        // then
        assertThat(pendingJobExecutionRequests.size()).isEqualTo(2);
        assertThat(submittedJobExecutionRequests.size()).isEqualTo(1);
        assertThat(processedJobExecutionRequests.size()).isEqualTo(1);
    }

    public void testFindAllJobExecutionRequests() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob", "my job"));
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("x=1").withStatus(JobExecutionRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));
        jobExecutionRequestRepository.save(newJobExecutionRequest().withJobId(1).withParameters("x=2").withStatus(JobExecutionRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));

        // when
        List<JobExecutionRequest> jobExecutionRequests = jobExecutionRequestRepository.findAllJobExecutionRequests();

        // then
        assertThat(jobExecutionRequests.size()).isEqualTo(2);
    }

    public void testUpdateJobExecutionRequest() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob", "my job"));
        JobExecutionRequest jobExecutionRequest = newJobExecutionRequest().withJobId(1).withStatus(JobExecutionRequestStatus.PENDING);
        jobExecutionRequestRepository.save(jobExecutionRequest);

        // when
        jobExecutionRequest.setStatus(JobExecutionRequestStatus.SUBMITTED);
        jobExecutionRequestRepository.update(jobExecutionRequest);

        // then
        JobExecutionRequest updatedJobExecutionRequest = jobExecutionRequestRepository.findJobExecutionRequestById(jobExecutionRequest.getId());
        assertThat(updatedJobExecutionRequest.getStatus()).isEqualTo(JobExecutionRequestStatus.SUBMITTED);
    }
}
