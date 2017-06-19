package org.jeasy.jobs;

import org.hibernate.SessionFactory;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.job.JobService;
import org.jeasy.jobs.request.JobRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jeasy.jobs.DataSourceConfiguration.*;

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
        hibernateProperties.setProperty("hibernate.dialect", hibernateDialect);
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
        localSessionFactoryBean.setHibernateProperties(hibernateProperties);
        localSessionFactoryBean.setMappingLocations(
                new ClassPathResource("job.hbm.xml"),
                new ClassPathResource("jobRequest.hbm.xml"),
                new ClassPathResource("jobExecution.hbm.xml")
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
    public JobRepository jobRepository() {
        return new JobRepository(sessionFactory());
    }

    @Bean
    public JobRequestRepository jobRequestRepository() {
        return new JobRequestRepository(sessionFactory());
    }

    @Bean
    public JobExecutionRepository jobExecutionRepository() {
        return new JobExecutionRepository(sessionFactory());
    }

    @Bean
    public JobService jobService() {
        return new JobService(jobExecutionRepository(), jobRequestRepository());
    }

    @Bean
    public DataSourceConfiguration dataSourceConfiguration() {
        DataSourceConfiguration defaultDataSourceConfiguration = DEFAULT_DATA_SOURCE_CONFIGURATION;
        String configurationPath = System.getProperty(DATA_SOURCE_CONFIGURATION_PROPERTY);
        try {
            if (configurationPath != null) {
                Properties properties = new Properties();
                properties.load(new FileReader(new File(configurationPath)));
                DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration();
                dataSourceConfiguration.setDatabaseUrl(properties.getProperty(DATA_SOURCE_CONFIGURATION_URL));
                dataSourceConfiguration.setDatabaseUser(properties.getProperty(DATA_SOURCE_CONFIGURATION_USER));
                dataSourceConfiguration.setDatabasePassword(properties.getProperty(DATA_SOURCE_CONFIGURATION_PASSWORD));
                return dataSourceConfiguration;
            } else {
                LOGGER.log(Level.INFO, "No data source configuration file specified, using default configuration: " + defaultDataSourceConfiguration);
                return defaultDataSourceConfiguration;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to read data source configuration from file " + configurationPath, e);
            LOGGER.log(Level.WARNING, "Using default data source configuration: " + defaultDataSourceConfiguration);
            return defaultDataSourceConfiguration;
        }
    }
}
