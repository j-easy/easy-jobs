package org.jeasy.jobs;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConfigUtils {

    private static final Logger LOGGER = Logger.getLogger(ConfigUtils.class.getName());
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream inputStream = ConfigUtils.class.getResourceAsStream("/conf.properties");
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
    }

    static Properties getProperties() {
        return properties;
    }

    static DataSource getDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:" + properties.getProperty("easy.jobs.database.url") + ";DATABASE_TO_UPPER=false");
        dataSource.setUser(properties.getProperty("easy.jobs.database.user"));
        dataSource.setPassword(properties.getProperty("easy.jobs.database.password"));
        return dataSource;
    }

}
