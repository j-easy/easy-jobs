package org.jeasy.jobs.server;

import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobDefinition;
import org.jeasy.jobs.job.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    private JobRepository jobRepository;

    @Autowired
    public DatabaseInitializer(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    void init(DataSource dataSource, JobDefinitions jobDefinitions) {
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        LOGGER.info("Loading job definitions from " + jobDefinitions.getSourceFile());
        for (JobDefinition jobDefinition : jobDefinitions.getJobDefinitions()) {
            String name = jobDefinition.getName();
            if (name == null) {
                name = JobDefinitions.getSimpleNameFrom(jobDefinition.getClazz());
                jobDefinition.setName(name);
            }
            LOGGER.info("Registering " + jobDefinition);
            jobRepository.save(new Job(jobDefinition.getId(), name, jobDefinition.getDescription()));
        }
    }
}
