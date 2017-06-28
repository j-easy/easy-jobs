package org.jeasy.jobs;

public class DataSourceConfiguration {

    public static final String DATA_SOURCE_CONFIGURATION_PROPERTY = "easy.jobs.database.config.file";
    public static final String DATA_SOURCE_CONFIGURATION_URL = "easy.jobs.database.config.url";
    public static final String DATA_SOURCE_CONFIGURATION_USER = "easy.jobs.database.config.user";
    public static final String DATA_SOURCE_CONFIGURATION_PASSWORD = "easy.jobs.database.config.password";
    public static final String DATA_SOURCE_CONFIGURATION_INIT = "easy.jobs.database.config.init";
    public static final DataSourceConfiguration DEFAULT_DATA_SOURCE_CONFIGURATION = new DataSourceConfiguration (
            "jdbc:h2:" + System.getProperty("user.home") + "/.easy-jobs/db;DATABASE_TO_UPPER=false",
            "admin",
            ""
    );

    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    public DataSourceConfiguration() {
    }

    public DataSourceConfiguration(String databaseUrl, String databaseUser, String databasePassword) {
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    @Override
    public String toString() {
        return "DataSourceConfiguration {" +
                "databaseUrl='" + databaseUrl + '\'' +
                ", databaseUser='" + databaseUser + '\'' +
                ", databasePassword='********'" +
                '}';
    }
}
