package org.jeasy.jobs.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class DefaultJob implements Callable<JobExitStatus> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJob.class);

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
            LOGGER.error("Processing of request with id " + requestId + " has failed", e);
            jobService.updateJobExecutionAndItsCorrespondingRequest(requestId, JobExitStatus.FAILED);
            return JobExitStatus.FAILED;
        }
    }

}
