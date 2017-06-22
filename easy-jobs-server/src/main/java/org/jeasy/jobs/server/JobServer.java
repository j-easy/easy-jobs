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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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
        String configurationPath = System.getProperty(JobDefinitions.JOBS_DEFINITIONS_CONFIGURATION_FILE_PARAMETER_NAME);
        if (configurationPath == null) {
            LOGGER.error("No jobs configuration file specified. Jobs configuration file is mandatory to load job definitions." +
                    "Please provide a JVM property -D" + JobDefinitions.JOBS_DEFINITIONS_CONFIGURATION_FILE_PARAMETER_NAME + "=/path/to/jobs/configuration/file");
        }
        JobDefinitions jobDefinitions = null;
        try {
            jobDefinitions = loadJobDefinitions(configurationPath);
        } catch (Exception e) {
            LOGGER.error("Unable to load jobs configuration from file " + configurationPath, e);
            System.exit(1);
        }
        JobServerConfiguration jobServerConfiguration = new JobServerConfiguration.Loader().loadServerConfiguration();
        LOGGER.info("Using job server configuration: " + jobServerConfiguration);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(getConfigurationClasses(), args);
        if (jobServerConfiguration.isDatabaseInit()) {
            DataSource dataSource = applicationContext.getBean(DataSource.class);
            LOGGER.info("Initializing database");
            init(dataSource, jobDefinitions, applicationContext);
        }

        Map<Integer, JobDefinition> jobDefinitionsMap = mapJobDefinitionsToJobIdentifiers(jobDefinitions);
        JobService jobService = applicationContext.getBean(JobService.class);
        jobService.setExecutorService(executorService(jobServerConfiguration.getWorkersNumber()));
        jobService.setJobDefinitions(jobDefinitionsMap);
        JobServer jobServer = new JobServer(jobService);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobServer, 0, jobServerConfiguration.getPollingInterval(), TimeUnit.SECONDS);
        LOGGER.info("Job server started");
        registerShutdownHook();
    }

    private static Object[] getConfigurationClasses() {
        return new Object[]{
                JobServer.class, ContextConfiguration.class, JobController.class,
                JobRequestController.class, JobExecutionController.class};
    }

    private static Map<Integer, JobDefinition> mapJobDefinitionsToJobIdentifiers(JobDefinitions definitions) {
        Map<Integer, JobDefinition> jobDefinitions = new HashMap<>();
        for (JobDefinition jobDefinition : definitions.getJobDefinitions()) {
            jobDefinitions.put(jobDefinition.getId(), jobDefinition);
        }
        return jobDefinitions;
    }

    private static void init(DataSource dataSource, JobDefinitions jobDefinitions, ApplicationContext ctx) {
        Resource resource = new ClassPathResource("database-schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        JobRepository jobRepository = ctx.getBean(JobRepository.class);
        LOGGER.info("Loading job definitions");
        for (JobDefinition jobDefinition : jobDefinitions.getJobDefinitions()) {
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

    private static JobDefinitions loadJobDefinitions(String configurationPath) throws Exception {
        return new JobDefinitions.Reader().read(new File(configurationPath));
    }

    private static ExecutorService executorService(int workersNumber) {
        final AtomicLong count = new AtomicLong(0);
        return Executors.newFixedThreadPool(workersNumber, r -> {
            Thread thread = new Thread(r);
            thread.setName("worker-thread-" + count.incrementAndGet()); // make this configurable: easy.jobs.server.config.workers.name.prefix
            return thread;
        });
    }

}
