package org.jeasy.jobs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(classes = {ContextConfiguration.class})
public class JobRequestDAOTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private JobRequestDAO jobRequestDAO;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testJobRequestPersistence() throws Exception {
        // given
        jobDAO.save(new Job(1, "MyJob"));

        // when
        jobRequestDAO.save(new JobRequest(1, "", JobRequestStatus.PENDING, LocalDateTime.now(), null));

        // then
        Integer nbJobRequests = jdbcTemplate.queryForObject("select count(*) from job_request", Integer.class);
        assertThat(nbJobRequests).isEqualTo(1);
    }
}
