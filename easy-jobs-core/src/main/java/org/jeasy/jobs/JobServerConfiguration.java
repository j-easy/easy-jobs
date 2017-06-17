package org.jeasy.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class JobServerConfiguration {

    static final String CONFIGURATION_PATH_PARAMETER_NAME = "easy.jobs.configuration.path";
    static final JobServerConfiguration DEFAULT_JOB_SERVER_CONFIGURATION =
            new JobServerConfiguration(
                    10,
                    30,
                    "h2",
                    true,
                    "jdbc:h2:" + System.getProperty("user.home") + "/.easy-jobs/db;DATABASE_TO_UPPER=false",
                    "admin",
                    "",
                    new ArrayList<>()
            );

    @JsonProperty("workers.number")
    private int workersNumber;
    @JsonProperty("polling.interval")
    private int pollingInterval;
    @JsonProperty("database.type")
    private String databaseType;
    @JsonProperty("database.init")
    private boolean databaseInit;
    @JsonProperty("database.url")
    private String databaseUrl;
    @JsonProperty("database.user")
    private String databaseUser;
    @JsonProperty("database.password")
    private String databasePassword;
    @JsonProperty("jobs")
    private List<JobDefinition> jobDefinitions;

    public JobServerConfiguration() {
    }

    public JobServerConfiguration(int workersNumber, int pollingInterval, String databaseType, boolean databaseInit, String databaseUrl, String databaseUser, String databasePassword, List<JobDefinition> jobDefinitions) {
        this.workersNumber = workersNumber;
        this.pollingInterval = pollingInterval;
        this.databaseType = databaseType;
        this.databaseInit = databaseInit;
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.jobDefinitions = jobDefinitions;
    }

    public int getWorkersNumber() {
        return workersNumber;
    }

    public void setWorkersNumber(int workersNumber) {
        this.workersNumber = workersNumber;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public boolean isDatabaseInit() {
        return databaseInit;
    }

    public void setDatabaseInit(boolean databaseInit) {
        this.databaseInit = databaseInit;
    }

    public List<JobDefinition> getJobDefinitions() {
        return jobDefinitions;
    }

    public void setJobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
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
        return "JobServerConfiguration {" +
                "workersNumber=" + workersNumber +
                ", pollingInterval=" + pollingInterval +
                ", databaseType='" + databaseType + '\'' +
                ", databaseInit=" + databaseInit +
                ", databaseUrl='" + databaseUrl + '\'' +
                ", databaseUser='" + databaseUser + '\'' +
                ", databasePassword='" + "**********" + '\'' +
                ", jobDefinitions=" + jobDefinitions +
                '}';
    }

    public static class JobDefinition {

        private int id;
        private String name;
        @JsonProperty("class")
        private String clazz;
        private String method;

        public JobDefinition() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return "Job {" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", class='" + clazz + '\'' +
                    ", method='" + method + '\'' +
                    '}';
        }
    }
}
