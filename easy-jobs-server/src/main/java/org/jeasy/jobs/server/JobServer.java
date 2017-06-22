package org.jeasy.jobs.server;

import org.jeasy.jobs.ContextConfiguration;
import org.jeasy.jobs.job.JobDefinition;
import org.jeasy.jobs.job.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;
import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@ComponentScan("org.jeasy.jobs.server")
public class JobServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServer.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        JobDefinitions jobDefinitions = failFastLoadJobDefinitions();
        failFastLoadJobs(jobDefinitions);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(new Object[]{JobServer.class, ContextConfiguration.class}, args);
        JobServerConfiguration jobServerConfiguration = new JobServerConfiguration.Loader().loadServerConfiguration();
        LOGGER.info("Using job server configuration: " + jobServerConfiguration);
        if (jobServerConfiguration.isDatabaseInit()) {
            DataSource dataSource = applicationContext.getBean(DataSource.class);
            LOGGER.info("Initializing database");
            DatabaseInitializer databaseInitializer = applicationContext.getBean(DatabaseInitializer.class);
            databaseInitializer.init(dataSource, jobDefinitions);
        }

        JobService jobService = applicationContext.getBean(JobService.class);
        jobService.setExecutorService(executorService(jobServerConfiguration.getWorkersPoolSize()));
        jobService.setJobDefinitions(jobDefinitions.mapJobDefinitionsToJobIdentifiers());
        JobRequestPoller jobRequestPoller = new JobRequestPoller(jobService);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobRequestPoller, 0, jobServerConfiguration.getPollingInterval(), TimeUnit.SECONDS);
        LOGGER.info("Job server started");
        registerShutdownHook();
    }

    private static JobDefinitions failFastLoadJobDefinitions() {
        JobDefinitions jobDefinitions = null;
        String configurationPath = System.getProperty(JobDefinitions.JOBS_DEFINITIONS_CONFIGURATION_FILE_PARAMETER_NAME);
        if (configurationPath == null) {
            LOGGER.error("No jobs configuration file specified. Jobs configuration file is mandatory to load job definitions." +
                    "Please provide a JVM property -D" + JobDefinitions.JOBS_DEFINITIONS_CONFIGURATION_FILE_PARAMETER_NAME + "=/path/to/jobs/configuration/file");
            System.exit(1);
        }
        try {
            jobDefinitions = new JobDefinitions.Reader().read(new File(configurationPath));
        } catch (Exception e) {
            LOGGER.error("Unable to load jobs configuration from file " + configurationPath, e);
            System.exit(1);
        }
        return jobDefinitions;
    }

    private static void failFastLoadJobs(JobDefinitions jobDefinitions) {
        JobDefinitions.Validator validator = new JobDefinitions.Validator();
        for (JobDefinition jobDefinition : jobDefinitions.getJobDefinitions()) {
            try {
                validator.validate(jobDefinition);
            } catch (JobDefinitions.InvalidJobDefinitionException e) {
                LOGGER.error("Unable to validate job definition " + jobDefinition, e);
                System.exit(1);
            }
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            LOGGER.info("Job manager stopped");
        }));
    }

    private static ExecutorService executorService(int workersPoolSize) {
        final AtomicLong count = new AtomicLong(0);
        return Executors.newFixedThreadPool(workersPoolSize, r -> {
            Thread thread = new Thread(r);
            String workerNamePrefix = System.getProperty(JobServerConfiguration.WORKERS_NAME_PREFIX_PARAMETER_NAME);
            if (workerNamePrefix == null) {
                workerNamePrefix = "worker-thread-";
            }
            thread.setName(workerNamePrefix + count.incrementAndGet());
            return thread;
        });
    }

}
