package org.jeasy.jobs;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    @Bean
    public LocalSessionFactoryBean localSessionFactoryBean() {
        Properties hibernateProperties = new Properties();
        // todo change according to user choice
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(datasource());
        localSessionFactoryBean.setHibernateProperties(hibernateProperties);
        localSessionFactoryBean.setMappingLocations(
                new ClassPathResource("org/jeasy/jobs/job.hbm.xml"),
                new ClassPathResource("org/jeasy/jobs/jobRequest.hbm.xml"),
                new ClassPathResource("org/jeasy/jobs/jobExecution.hbm.xml")
        );
        return localSessionFactoryBean;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return localSessionFactoryBean().getObject();
    }

    @Bean
    public DataSource datasource() {
        // todo change according to user choice
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:.;DATABASE_TO_UPPER=false");
        dataSource.setUser("admin");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public JobDAO jobDAO() {
        return new JobDAO(sessionFactory());
    }

    @Bean
    public JobRequestDAO jobRequestDAO() {
        return new JobRequestDAO(sessionFactory());
    }

    @Bean
    public JobExecutionDAO jobExecutionDAO() {
        return new JobExecutionDAO(sessionFactory());
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(workers(), workerThreadFactory());
    }

    @Bean
    public JobFactory jobFactory() {
        return new JobFactory();
    }

    @Bean
    public JobService jobService() {
        return new JobService(executorService(), jobExecutionDAO(), jobRequestDAO(), jobFactory());
    }

    @Bean
    public WorkerThreadFactory workerThreadFactory() {
        return new WorkerThreadFactory();
    }

    public int workers() {
        int workers = 10;
        if (System.getProperty("easy.jobs.workers.number") != null) {
            workers = Integer.parseInt(System.getProperty("easy.jobs.workers.number"));
        }
        LOGGER.info("I will use " + workers + " workers to run jobs");
        return workers;
    }

    @Bean
    public int pollingInterval() {
        int polling = 30;
        if (System.getProperty("easy.jobs.polling.interval") != null) {
            polling = Integer.parseInt(System.getProperty("easy.jobs.polling.interval"));
        }
        LOGGER.info("I will poll pending job requests every " + polling + "s");
        return polling;
    }

    @Bean
    public Properties configurationProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = Configuration.class.getResourceAsStream("/conf.properties");
            String configuration = System.getProperty("easy.jobs.config");
            if (configuration != null) {
                LOGGER.log(Level.INFO, "Loading configuration properties from: " + configuration);
                inputStream = new FileInputStream(configuration);
            }
            properties.load(inputStream);
            if (configuration == null) {
                properties.setProperty("easy.jobs.database.url", System.getProperty("user.dir") + System.getProperty("file.separator") +  "easy-jobs-db" );
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load configuration properties", e);
        }
        return properties;
    }
}
