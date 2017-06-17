package org.jeasy.jobs.job;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultJob implements Callable<JobExitStatus> {

    private static final Logger LOGGER = Logger.getLogger(DefaultJob.class.getName());

    private int requestId;
    private JobService jobService;

    private Object target;
    private Method method;

    public DefaultJob(int requestId, Object target, Method method, JobService jobService) {
        this.requestId = requestId;
        this.target = target;
        this.method = method;
        this.jobService = jobService;
    }

    public final JobExitStatus call() {
        try {
            LOGGER.info("Processing job request with id " + requestId);
            method.invoke(target);
            jobService.updateJobExecutionAndItsCorrespondingRequest(requestId, JobExitStatus.SUCCEEDED);
            LOGGER.info("Successfully processed job request with id " + requestId);
            return JobExitStatus.SUCCEEDED;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Processing of request with id " + requestId + " has failed", e);
            jobService.updateJobExecutionAndItsCorrespondingRequest(requestId, JobExitStatus.FAILED);
            return JobExitStatus.FAILED;
        }
    }

}
