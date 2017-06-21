package org.jeasy.jobs.server;

import org.jeasy.jobs.ContextConfiguration;
import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobDefinition;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.job.JobService;
import org.jeasy.jobs.server.web.JobController;
import org.jeasy.jobs.server.web.JobExecutionController;
import org.jeasy.jobs.server.web.JobRequestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
public class JobServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServer.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private JobService jobService;

    private JobServer(JobService jobService) {
        this.jobService = jobService;
    }

    public JobServer() {
    }

    @Override
    public void run() {
        jobService.pollRequestsAndSubmitJobs();
    }

    public static void main(String[] args) {
        registerShutdownHook();
        ConfigurableApplicationContext applicationContext = SpringApplication.run(getConfigurationClasses(), args);
        JobServerConfiguration jobServerConfiguration = getServerConfiguration();
        LOGGER.info("Using job server configuration: " + jobServerConfiguration);
        if (jobServerConfiguration.isDatabaseInit()) {
            DataSource dataSource = applicationContext.getBean(DataSource.class);
            LOGGER.info("Initializing database");
            init(dataSource, jobServerConfiguration, applicationContext);
        }

        Map<Integer, JobDefinition> jobDefinitions = getJobDefinitions(jobServerConfiguration);
        JobService jobService = applicationContext.getBean(JobService.class);
        jobService.setExecutorService(executorService());
        jobService.setJobDefinitions(jobDefinitions);
        JobServer jobServer = new JobServer(jobService);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobServer, 0, jobServerConfiguration.getPollingInterval(), TimeUnit.SECONDS);
        LOGGER.info("Job server started");
    }

    private static Object[] getConfigurationClasses() {
        return new Object[]{
                JobServer.class, ContextConfiguration.class, JobController.class,
                JobRequestController.class, JobExecutionController.class};
    }

    private static Map<Integer, JobDefinition> getJobDefinitions(JobServerConfiguration jobServerConfiguration) {
        Map<Integer, JobDefinition> jobDefinitions = new HashMap<>();
        for (JobDefinition jobDefinition : jobServerConfiguration.getJobDefinitions()) {
            jobDefinitions.put(jobDefinition.getId(), jobDefinition);
        }
        return jobDefinitions;
    }

    private static void init(DataSource dataSource, JobServerConfiguration jobServerConfiguration, ApplicationContext ctx) {
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        JobRepository jobRepository = ctx.getBean(JobRepository.class);
        LOGGER.info("Loading job definitions");
        for (JobDefinition jobDefinition : jobServerConfiguration.getJobDefinitions()) {
            String name = jobDefinition.getName();
            if (name == null) {
                name = jobDefinition.getClazz(); // todo get simple class name from fully qualified name
            }
            LOGGER.info("Registering " + jobDefinition);
            jobRepository.save(new Job(jobDefinition.getId(), name));
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            LOGGER.info("Job manager stopped");
        }));
    }

    private static JobServerConfiguration getServerConfiguration() {
        JobServerConfiguration defaultJobServerConfiguration = JobServerConfiguration.DEFAULT_JOB_SERVER_CONFIGURATION;
        String configurationPath = System.getProperty(JobServerConfiguration.CONFIGURATION_PATH_PARAMETER_NAME);
        try {
            if (configurationPath != null) {
                return new JobServerConfigurationReader().read(new File(configurationPath));
            } else {
                LOGGER.info("No configuration file specified, using default configuration: " + defaultJobServerConfiguration);
                return defaultJobServerConfiguration;
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to read configuration from file " + configurationPath, e);
            // FIXME Should easy jobs introspect and validate job definitions (existing method, etc) ? I guess yes
            LOGGER.warn("Using default configuration: " + defaultJobServerConfiguration);
            return defaultJobServerConfiguration;
        }
    }

    private static ExecutorService executorService() {
        return Executors.newFixedThreadPool(getServerConfiguration().getWorkersNumber(), new WorkerThreadFactory());
    }

}
