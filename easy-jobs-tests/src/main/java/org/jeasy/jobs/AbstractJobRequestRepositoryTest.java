package org.jeasy.jobs;

import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jeasy.jobs.request.JobRequest.newJobRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public abstract class AbstractJobRequestRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobRequestRepository jobRequestRepository;

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

    public void testJobRequestPersistence() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob"));

        // when
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("").withStatus(JobRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));

        // then
        Integer nbJobRequests = jdbcTemplate.queryForObject("select count(*) from ej_job_request", Integer.class);
        assertThat(nbJobRequests).isEqualTo(1);
    }

    public void testFindJobRequestsByStatus() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob"));
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("").withStatus(JobRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("").withStatus(JobRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("").withStatus(JobRequestStatus.SUBMITTED).withCreationDate(LocalDateTime.now()));
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("").withStatus(JobRequestStatus.PROCESSED).withCreationDate(LocalDateTime.now()).withProcessingDate(LocalDateTime.now().plus(2, ChronoUnit.MINUTES)));

        // when
        List<JobRequest> pendingJobRequests = jobRequestRepository.findJobRequestsByStatus(JobRequestStatus.PENDING);
        List<JobRequest> submittedJobRequests = jobRequestRepository.findJobRequestsByStatus(JobRequestStatus.SUBMITTED);
        List<JobRequest> processedJobRequests = jobRequestRepository.findJobRequestsByStatus(JobRequestStatus.PROCESSED);

        // then
        assertThat(pendingJobRequests.size()).isEqualTo(2);
        assertThat(submittedJobRequests.size()).isEqualTo(1);
        assertThat(processedJobRequests.size()).isEqualTo(1);
    }

    public void testFindAllJobRequests() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob"));
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("x=1").withStatus(JobRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));
        jobRequestRepository.save(newJobRequest().withJobId(1).withParameters("x=2").withStatus(JobRequestStatus.PENDING).withCreationDate(LocalDateTime.now()));

        // when
        List<JobRequest> jobRequests = jobRequestRepository.findAllJobRequests();

        // then
        assertThat(jobRequests.size()).isEqualTo(2);
    }

    public void testUpdateJobRequest() throws Exception {
        // given
        jobRepository.save(new Job(1, "MyJob"));
        JobRequest jobRequest = newJobRequest().withJobId(1).withStatus(JobRequestStatus.PENDING);
        jobRequestRepository.save(jobRequest);

        // when
        jobRequest.setStatus(JobRequestStatus.SUBMITTED);
        jobRequestRepository.update(jobRequest);

        // then
        JobRequest updatedJobRequest = jobRequestRepository.findById(jobRequest.getId());
        assertThat(updatedJobRequest.getStatus()).isEqualTo(JobRequestStatus.SUBMITTED);
    }
}
