package org.jeasy.jobs.server;

import org.jeasy.jobs.ContextConfiguration;
import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.job.JobDefinition;
import org.jeasy.jobs.job.JobService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        JobServerConfiguration jobServerConfiguration = getServerConfiguration();
        LOGGER.info("Using job server configuration: " + jobServerConfiguration);
        if (jobServerConfiguration.isDatabaseInit()) {
            DataSource dataSource = ctx.getBean(DataSource.class);
            LOGGER.info("Initializing database");
            init(dataSource, jobServerConfiguration, ctx);
        }

        Map<Integer, JobDefinition> jobDefinitions = getJobDefinitions(jobServerConfiguration);
        JobService jobService = ctx.getBean(JobService.class);
        jobService.setExecutorService(executorService());
        jobService.setJobDefinitions(jobDefinitions);
        JobServer jobServer = new JobServer(jobService);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobServer, 0, jobServerConfiguration.getPollingInterval(), TimeUnit.SECONDS);
        LOGGER.info("Job server started");
        writePidToFile();
    }

    private static Map<Integer, JobDefinition> getJobDefinitions(JobServerConfiguration jobServerConfiguration) {
        Map<Integer, JobDefinition> jobDefinitions = new HashMap<>();
        for (JobDefinition jobDefinition : jobServerConfiguration.getJobDefinitions()) {
            jobDefinitions.put(jobDefinition.getId(), jobDefinition);
        }
        return jobDefinitions;
    }

    private static void printBanner() {
        // TODO banner with ascii art and version number
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
            LOGGER.info("Job manager stopped"); }));
    }

    private static JobServerConfiguration getServerConfiguration() {
        JobServerConfiguration defaultJobServerConfiguration = JobServerConfiguration.DEFAULT_JOB_SERVER_CONFIGURATION;
        String configurationPath = System.getProperty(JobServerConfiguration.CONFIGURATION_PATH_PARAMETER_NAME);
        try {
            if (configurationPath != null) {
                return new JobServerConfigurationReader().read(new File(configurationPath));
            } else {
                LOGGER.log(Level.INFO, "No configuration file specified, using default configuration: " + defaultJobServerConfiguration);
                return defaultJobServerConfiguration;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to read configuration from file " + configurationPath, e);
            // FIXME Should easy jobs introspect and validate job definitions (existing method, etc) ? I guess yes
            LOGGER.log(Level.WARNING, "Using default configuration: " + defaultJobServerConfiguration);
            return defaultJobServerConfiguration;
        }
    }

    private static ExecutorService executorService() {
        return Executors.newFixedThreadPool(getServerConfiguration().getWorkersNumber(), new WorkerThreadFactory());
    }

    private static void writePidToFile() {
        FileWriter pidWriter = null;
        try {
            pidWriter = new FileWriter("process.id");
            String pidAtHost = ManagementFactory.getRuntimeMXBean().getName();
            String pid = pidAtHost.substring(0, pidAtHost.indexOf('@'));
            pidWriter.write(pid);
            pidWriter.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to write process id", e);
        } finally {
            try {
                if (pidWriter != null) {
                    pidWriter.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Unable close process id writer", e);
            }
        }
    }

}
