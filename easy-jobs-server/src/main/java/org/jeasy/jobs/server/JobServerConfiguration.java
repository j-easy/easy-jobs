package org.jeasy.jobs.server;

import org.jeasy.jobs.DataSourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServerConfiguration.class);

    static final String WORKERS_POOL_SIZE_PARAMETER_NAME = "easy.jobs.server.config.workers.pool.size";
    static final String WORKERS_NAME_PREFIX_PARAMETER_NAME = "easy.jobs.server.config.workers.name.prefix";
    static final String POLLING_INTERVAL_PARAMETER_NAME = "easy.jobs.server.config.polling.interval";

    private int workersPoolSize = 10; // todo rename to workers pool size
    private int pollingInterval = 30;
    private boolean databaseInit = false;

    public int getWorkersPoolSize() {
        return workersPoolSize;
    }

    public void setWorkersPoolSize(int workersPoolSize) {
        this.workersPoolSize = workersPoolSize;
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
                "workersPoolSize=" + workersPoolSize +
                ", pollingInterval=" + pollingInterval +
                ", databaseInit=" + databaseInit +
                '}';
    }

    static class Loader {

        JobServerConfiguration loadServerConfiguration() {
            JobServerConfiguration jobServerConfiguration = new JobServerConfiguration();
            loadWorkersPoolSizeParameter(jobServerConfiguration);
            loadPollingIntervalParameter(jobServerConfiguration);
            loadDatabaseInitParameter(jobServerConfiguration);
            return jobServerConfiguration;
        }

        // TODO use Easy Props to inject these parameters in a declarative way and get rid of this boilerplate

        private void loadDatabaseInitParameter(JobServerConfiguration jobServerConfiguration) {
            String databaseInitParameter = System.getProperty(DataSourceConfiguration.DATA_SOURCE_CONFIGURATION_INIT);
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

        private void loadWorkersPoolSizeParameter(JobServerConfiguration jobServerConfiguration) {
            String workersPoolSizeParameter = System.getProperty(JobServerConfiguration.WORKERS_POOL_SIZE_PARAMETER_NAME);
            if (workersPoolSizeParameter != null) {
                try {
                    int workersNumber = Integer.parseInt(workersPoolSizeParameter);
                    jobServerConfiguration.setWorkersPoolSize(workersNumber);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Unable to read workers pool size parameter value, I will use the default value:" + jobServerConfiguration.getWorkersPoolSize(), e);
                }
            }
        }
    }
}
