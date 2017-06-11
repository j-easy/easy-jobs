package org.jeasy.jobs;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;

class JobExecutionDAO {

    private JdbcTemplate jdbcTemplate;

    public JobExecutionDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(JobExecution jobExecution) {
        String insert = "INSERT INTO job_execution (REQUEST_ID, STATUS, START_DATE) VALUES (?,?,?)";
        Object[] params = new Object[]{
                jobExecution.getRequestId(),
                JobExecutionStatus.RUNNING,
                LocalDateTime.now()
        };
        int[] types = new int[]{Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP};
        jdbcTemplate.update(insert, params, types);
    }

    public void update(int jobRequestId, JobExitStatus jobExitStatus, LocalDateTime endDate) {
        String insert = "UPDATE job_execution SET STATUS = ?,  JOB_STATUS = ?, END_DATE = ? WHERE REQUEST_ID = ?";
        Object[] params = new Object[]{
                JobExecutionStatus.FINISHED,
                jobExitStatus,
                endDate,
                jobRequestId
        };
        int[] types = new int[]{Types.VARCHAR,Types.VARCHAR,Types.TIMESTAMP, Types.INTEGER};
        jdbcTemplate.update(insert, params, types);
    }

}
