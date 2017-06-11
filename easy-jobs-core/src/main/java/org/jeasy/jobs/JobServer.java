package org.jeasy.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

        String jobsDescriptors = "jobs.json";
        if(args.length >= 1 ) {
            jobsDescriptors = args[0];
        }
        File file = new File(jobsDescriptors);
        LOGGER.info("Loading job definitions from " + file.getAbsolutePath());
        List<JobDefinition> jobDefinitions = null;
        try {
            jobDefinitions = getJobDefinitions(file);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load job definitions from " + file.getAbsolutePath(), e);
            System.exit(1);
        }
        for (JobDefinition jobDefinition : jobDefinitions) {
            LOGGER.info("Registering " + jobDefinition);
        }
        JobFactory jobFactory = new JobFactory(jobDefinitions);

        int workers = 10;
        if (System.getProperty("easy.jobs.workers.number") != null) {
            workers = Integer.parseInt(System.getProperty("easy.jobs.workers.number"));
        }
        LOGGER.info("I will use " + workers + " workers to run jobs");
        int polling = 30;
        if (System.getProperty("easy.jobs.polling.interval") != null) {
            polling = Integer.parseInt(System.getProperty("easy.jobs.polling.interval"));
        }
        LOGGER.info("I will poll pending job requests every " + polling + "s");

        DataSource dataSource = ConfigUtils.getDataSource();
        if (System.getProperty("easy.jobs.database.init") != null) {
            boolean initDatabase = Boolean.parseBoolean(System.getProperty("easy.jobs.database.init"));
            if (initDatabase) {
                LOGGER.info("Initializing database in " + ConfigUtils.getProperties().getProperty("easy.jobs.database.url"));
                init(dataSource, jobDefinitions);
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(workers, new WorkerThreadFactory());
        JobRequestDAO jobRequestDAO = new JobRequestDAO(dataSource);
        JobExecutionDAO jobExecutionDAO = new JobExecutionDAO(dataSource);
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        JobService jobService = new JobService(executorService, jobExecutionDAO, jobRequestDAO, transactionTemplate, jobFactory);
        jobFactory.setJobService(jobService);
        JobServer jobServer = new JobServer(jobService);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobServer, 0, polling, TimeUnit.SECONDS);
        LOGGER.info("Job manager started.");

    }

    private static void printBanner() {
        // TODO banner with ascii art and version number
    }

    private static void init(DataSource dataSource, List<JobDefinition> jobDefinitions) {
        Resource resource = new ClassPathResource("schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        JobDAO jobDAO = new JobDAO(dataSource);
        for (JobDefinition jobDefinition : jobDefinitions) {
            String name = jobDefinition.getName();
            if (name == null) {
                name = jobDefinition.getClazz(); // todo get simple class name from fully qualified name
            }
            jobDAO.save(new Job(jobDefinition.getId(), name));
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            LOGGER.info("Job manager stopped."); }));
    }

    private static List<JobDefinition> getJobDefinitions(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(file, JobDefinition[].class));
    }

}
