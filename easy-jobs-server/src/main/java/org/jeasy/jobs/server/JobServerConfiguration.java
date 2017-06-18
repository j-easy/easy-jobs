package org.jeasy.jobs.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jeasy.jobs.job.JobDefinition;

import java.util.ArrayList;
import java.util.List;

public class JobServerConfiguration {

    static final String CONFIGURATION_PATH_PARAMETER_NAME = "easy.jobs.server.config.file";
    static final JobServerConfiguration DEFAULT_JOB_SERVER_CONFIGURATION =
            new JobServerConfiguration(
                    10,
                    30,
                    true,
                    new ArrayList<>()
            );

    @JsonProperty("workers.number")
    private int workersNumber;
    @JsonProperty("polling.interval")
    private int pollingInterval;
    @JsonProperty("database.init")
    private boolean databaseInit;
    @JsonProperty("jobs")
    private List<JobDefinition> jobDefinitions;

    public JobServerConfiguration() {
    }

    public JobServerConfiguration(int workersNumber, int pollingInterval, boolean databaseInit,List<JobDefinition> jobDefinitions) {
        this.workersNumber = workersNumber;
        this.pollingInterval = pollingInterval;
        this.databaseInit = databaseInit;
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

    @Override
    public String toString() {
        return "JobServerConfiguration {" +
                "workersNumber=" + workersNumber +
                ", pollingInterval=" + pollingInterval +
                ", databaseInit=" + databaseInit +
                ", jobDefinitions=" + jobDefinitions +
                '}';
    }
}
