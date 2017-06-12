package org.jeasy.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.*;
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
        Map<Integer, JobDefinition> jobDefinitions = null;
        try {
            jobDefinitions = getJobDefinitions(file);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load job definitions from " + file.getAbsolutePath(), e);
            System.exit(1);
        }
        for (JobDefinition jobDefinition : jobDefinitions.values()) {
            LOGGER.info("Registering " + jobDefinition);
        }

        ApplicationContext ctx = new AnnotationConfigApplicationContext(Configuration.class);
        Properties properties = (Properties) ctx.getBean("configurationProperties");
        DataSource dataSource = ctx.getBean(DataSource.class);
        if (System.getProperty("easy.jobs.database.init") != null) {
            boolean initDatabase = Boolean.parseBoolean(System.getProperty("easy.jobs.database.init"));
            if (initDatabase) {
                LOGGER.info("Initializing database in " + properties.getProperty("easy.jobs.database.url"));
                init(dataSource, jobDefinitions, ctx);
            }
        }

        JobFactory jobFactory = ctx.getBean(JobFactory.class);
        jobFactory.setJobs(jobDefinitions);
        JobService jobService = ctx.getBean(JobService.class);
        jobFactory.setJobService(jobService);
        JobServer jobServer = new JobServer(jobService);
        int polling = (Integer)ctx.getBean("pollingInterval");
        System.out.println("polling = " + polling);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(jobServer, 0, polling, TimeUnit.SECONDS);
        LOGGER.info("Job manager started.");

    }

    private static void printBanner() {
        // TODO banner with ascii art and version number
    }

    private static void init(DataSource dataSource, Map<Integer, JobDefinition> jobDefinitions, ApplicationContext ctx) {
        Resource resource = new ClassPathResource("schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
        JobDAO jobDAO = ctx.getBean(JobDAO.class);
        for (JobDefinition jobDefinition : jobDefinitions.values()) {
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

    private static Map<Integer, JobDefinition> getJobDefinitions(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<JobDefinition> jobDefinitionList = Arrays.asList(mapper.readValue(file, JobDefinition[].class));
        Map<Integer, JobDefinition> jobDefinitionMap = new HashMap<>();
        for (JobDefinition jobDefinition : jobDefinitionList) {
            jobDefinitionMap.put(jobDefinition.getId(), jobDefinition);
        }
        return jobDefinitionMap;
    }

}
