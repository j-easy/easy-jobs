package org.jeasy.jobs;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;

class JobDAO {

    private JdbcTemplate jdbcTemplate;

    public JobDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(Job job) {
        String insert = "INSERT INTO job (ID, NAME) VALUES (?,?)";
        Object[] params = new Object[]{
                job.getId(),
                job.getName(),
        };
        int[] types = new int[]{Types.INTEGER, Types.VARCHAR};
        jdbcTemplate.update(insert, params, types);
    }

}
