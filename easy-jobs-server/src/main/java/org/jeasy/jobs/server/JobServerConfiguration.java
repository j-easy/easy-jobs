package org.jeasy.jobs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServerConfiguration.class);

    static final String WORKERS_NUMBER_PARAMETER_NAME = "easy.jobs.server.config.workers.number";
    static final String POLLING_INTERVAL_PARAMETER_NAME = "easy.jobs.server.config.polling.interval";
    static final String DATABASE_INIT_PARAMETER_NAME = "easy.jobs.server.config.database.init";

    private int workersNumber = 10; // todo rename to workers pool size
    private int pollingInterval = 30;
    private boolean databaseInit = false;

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

    @Override
    public String toString() {
        return "JobServerConfiguration {" +
                "workersNumber=" + workersNumber +
                ", pollingInterval=" + pollingInterval +
                ", databaseInit=" + databaseInit +
                '}';
    }

    static class Loader {

        JobServerConfiguration loadServerConfiguration() {
            JobServerConfiguration jobServerConfiguration = new JobServerConfiguration();
            loadWorkersNumberParameter(jobServerConfiguration);
            loadPollingIntervalParameter(jobServerConfiguration);
            loadDatabaseInitParameter(jobServerConfiguration);
            return jobServerConfiguration;
        }

        // TODO use Easy Props to inject these parameters in a declarative way and get rid of this boilerplate

        private void loadDatabaseInitParameter(JobServerConfiguration jobServerConfiguration) {
            String databaseInitParameter = System.getProperty(JobServerConfiguration.DATABASE_INIT_PARAMETER_NAME);
            if (databaseInitParameter != null) {
                try {
                    boolean databaseInit = Boolean.parseBoolean(databaseInitParameter);
                    jobServerConfiguration.setDatabaseInit(databaseInit);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Unable to read database init parameter value, I will use the default value:" + jobServerConfiguration.isDatabaseInit(), e);
                }
            }
        }

        private void loadPollingIntervalParameter(JobServerConfiguration jobServerConfiguration) {
            String pollingIntervalParameter = System.getProperty(JobServerConfiguration.POLLING_INTERVAL_PARAMETER_NAME);
            if (pollingIntervalParameter != null) {
                try {
                    int pollingInterval = Integer.parseInt(pollingIntervalParameter);
                    jobServerConfiguration.setPollingInterval(pollingInterval);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Unable to read polling interval parameter value, I will use the default value:" + jobServerConfiguration.getPollingInterval(), e);
                }
            }
        }

        private void loadWorkersNumberParameter(JobServerConfiguration jobServerConfiguration) {
            String workersNumberParameter = System.getProperty(JobServerConfiguration.WORKERS_NUMBER_PARAMETER_NAME);
            if (workersNumberParameter != null) {
                try {
                    int workersNumber = Integer.parseInt(workersNumberParameter);
                    jobServerConfiguration.setWorkersNumber(workersNumber);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Unable to read workers number parameter value, I will use the default value:" + jobServerConfiguration.getWorkersNumber(), e);
                }
            }
        }
    }
}
