package org.jeasy.jobs;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JobServer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(JobServer.class.getName());
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    static {
        try {
            if (System.getProperty("java.util.logging.config.file") == null &&
                    System.getProperty("java.util.logging.config.class") == null) {
                LogManager.getLogManager().readConfiguration(JobServer.class.getResourceAsStream("/logging.properties"));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to load logging configuration properties", e);
        }
    }

    private JobService jobService;

    private JobServer(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public void run() {
        jobService.pollRequestsAndSubmitJobs();
    }

    public static void main(String[] args) {
        registerShutdownHook();
        printBanner();

        ApplicationContext ctx = new AnnotationConfigApplicationContext(ContextConfiguration.class);
        JobServerConfiguration jobServerConfiguration = ctx.getBean(JobServerConfiguration.class);
        if (jobServerConfiguration.isDatabaseInit()) {
            DataSource dataSource = ctx.getBean(DataSource.class);
            init(dataSource, jobServerConfiguration, ctx);
        }

        JobFactory jobFactory = ctx.getBean(JobFactory.class);
        Map<Integer, JobServerConfiguration.JobDefinition> jobDefinitions = getJobDefinitions(jobServerConfiguration);
        jobFactory.setJobs(jobDefinitions);
        JobService jobService = ctx.getBean(JobService.class);
        jobFactory.setJobService(jobService);
        JobServer jobServer = new JobServer(jobService);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobServer, 0, jobServerConfiguration.getPollingInterval(), TimeUnit.SECONDS);
        LOGGER.info("Job manager started.");

    }

    private static Map<Integer, JobServerConfiguration.JobDefinition> getJobDefinitions(JobServerConfiguration jobServerConfiguration) {
        Map<Integer, JobServerConfiguration.JobDefinition> jobDefinitions = new HashMap<>();
        for (JobServerConfiguration.JobDefinition jobDefinition : jobServerConfiguration.getJobDefinitions()) {
            jobDefinitions.put(jobDefinition.getId(), jobDefinition);
        }
        return jobDefinitions;
    }

    private static void printBanner() {
        // TODO banner with ascii art and version number
    }

    private static void init(DataSource dataSource, JobServerConfiguration jobServerConfiguration, ApplicationContext ctx) {
        Resource resource = new ClassPathResource("schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        JobDAO jobDAO = ctx.getBean(JobDAO.class);
        for (JobServerConfiguration.JobDefinition jobDefinition : jobServerConfiguration.getJobDefinitions()) {
            String name = jobDefinition.getName();
            if (name == null) {
                name = jobDefinition.getClazz(); // todo get simple class name from fully qualified name
            }
            LOGGER.info("Registering " + jobDefinition);
            jobDAO.save(new Job(jobDefinition.getId(), name));
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            LOGGER.info("Job manager stopped."); }));
    }

}
