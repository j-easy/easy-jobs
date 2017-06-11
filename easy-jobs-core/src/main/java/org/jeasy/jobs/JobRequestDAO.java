package org.jeasy.jobs;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

class JobRequestDAO {

    private JdbcTemplate jdbcTemplate;

    public JobRequestDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(JobRequest jobRequest) {
        String insert = "INSERT INTO job_request (JOB_ID, PARAMETERS, STATUS, CREATION_DATE) VALUES (?,?,?,?)";
        Object[] params = new Object[]{
                jobRequest.getJobId(),
                jobRequest.getParameters(),
                JobRequestStatus.PENDING,
                LocalDateTime.now()
        };
        int[] types = new int[]{Types.INTEGER,Types.VARCHAR,Types.VARCHAR, Types.TIMESTAMP};
        jdbcTemplate.update(insert, params, types);
    }

    public void updateStatusAndProcessingDate(int jobRequestId, JobRequestStatus status, LocalDateTime processingDate) {
        String insert = "UPDATE job_request SET STATUS = ?, PROCESSING_DATE = ? WHERE ID = ?";
        Object[] params = new Object[]{
                status,
                processingDate,
                jobRequestId
        };
        int[] types = new int[]{Types.VARCHAR,Types.TIMESTAMP, Types.INTEGER};
        jdbcTemplate.update(insert, params, types);
    }

    public void updateStatus(int jobRequestId, JobRequestStatus status) {
        String insert = "UPDATE job_request SET STATUS = ? WHERE ID = ?";
        Object[] params = new Object[]{
                status,
                jobRequestId
        };
        int[] types = new int[]{Types.VARCHAR, Types.INTEGER};
        jdbcTemplate.update(insert, params, types);
    }

    List<JobRequest> getPendingJobRequests() {
        // TODO order by priority (keep something for v2)
        String select = "SELECT id, job_id, parameters from job_request where STATUS = ?";
        Object[] params = new Object[]{
                JobRequestStatus.PENDING
        };
        int[] types = new int[]{Types.VARCHAR};

        return jdbcTemplate.query(select, params, types, (rs, rowNum) -> {
            int id = rs.getInt("id");
            int jobId = rs.getInt("job_id");
            String parameters = rs.getString("parameters");
            return new JobRequest(id, jobId, parameters, null, null, null); // No need for other fields for now
        });
    }

}
