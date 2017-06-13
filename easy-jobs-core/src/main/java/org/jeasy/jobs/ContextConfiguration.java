package org.jeasy.jobs;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
@ImportResource("classpath:data-source-config.xml")
public class ContextConfiguration {

    private static final Logger LOGGER = Logger.getLogger(ContextConfiguration.class.getName());

    @Autowired
    private DataSource dataSource;
    @Autowired
    private String hibernateDialect;

    @Bean
    public LocalSessionFactoryBean localSessionFactoryBean() {
        Properties hibernateProperties = new Properties();
        // todo change according to user choice
        hibernateProperties.setProperty("hibernate.dialect", hibernateDialect);
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
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
        return Executors.newFixedThreadPool(serverConfiguration().getWorkersNumber(), workerThreadFactory());
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

    @Bean
    public JobServerConfigurationReader configurationReader() {
        return new JobServerConfigurationReader();
    }

    @Bean
    public JobServerConfiguration serverConfiguration() {
        JobServerConfiguration defaultJobServerConfiguration = JobServerConfiguration.defaultJobServerConfiguration;
        String configurationPath = System.getProperty(JobServerConfiguration.CONFIGURATION_PATH_PARAMETER_NAME);
        try {
            if (configurationPath != null) {
                return configurationReader().read(new File(configurationPath));
            } else {
                LOGGER.log(Level.INFO, "No configuration file specified, using default configuration: " + defaultJobServerConfiguration);
                return defaultJobServerConfiguration;
            }
        } catch (Exception e) {
           LOGGER.log(Level.WARNING, "Unable to read configuration from file " + configurationPath, e);
           // FIXME may be fail fast is better? Should easy jobs introspect and validate job definitions (existing method, etc). I guess yes
            LOGGER.log(Level.WARNING, "Using default configuration: " + defaultJobServerConfiguration);
            return defaultJobServerConfiguration;
        }
    }

}
