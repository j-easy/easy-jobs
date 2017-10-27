package org.jeasy.jobs.server;

import org.apache.commons.beanutils.BeanUtils;
import org.jeasy.jobs.Utils;
import org.jeasy.jobs.job.JobDefinition;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.Service;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class JobExecutionRequestPoller implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionRequestPoller.class);

    private Service jobService;
    private ExecutorService executorService;
    private Map<Integer, JobDefinition> jobDefinitions;
    private ClassLoader jobClassLoader;

    public JobExecutionRequestPoller(Service jobService, ExecutorService executorService, Map<Integer, JobDefinition> jobDefinitions, ClassLoader jobClassLoader) {
        this.jobService = jobService;
        this.executorService = executorService;
        this.jobDefinitions = jobDefinitions;
        this.jobClassLoader = jobClassLoader;
    }

    @Override
    public void run() {
        List<JobExecutionRequest> pendingJobExecutionRequests = jobService.findPendingRequests(); // add limit 10 (nb workers) and you have throttling/back pressure for free!
        if (pendingJobExecutionRequests.isEmpty()) {
            return;
        }
        LOGGER.info("Found " + pendingJobExecutionRequests.size() + " pending job request(s)");
        for (JobExecutionRequest pendingJobExecutionRequest : pendingJobExecutionRequests) {
            int requestId = pendingJobExecutionRequest.getId();
            int jobId = pendingJobExecutionRequest.getJobId();
            String parameters = pendingJobExecutionRequest.getParameters();
            if (jobService.findJobById(jobId) == null) { // this is an extra check on the core side, the web controller already filters bad requests upfront
                LOGGER.warn("Unable to find a job with id " + jobId + ". Ignoring request with id " + requestId);
                continue;
            }
            // todo add allowsConcurrent parameter: if there is already an execution for the job, don't create a new job and don't saveJobExecutionAndUpdateItsCorrespondingRequest, the request will be picked up in the next run
            LOGGER.info("Creating a new job for request n° " + requestId + " with parameters [" + parameters + "]");
            Callable<JobExitStatus> job;
            try {
                job = createJob(jobId, requestId, parameters);
                jobService.saveJobExecutionAndUpdateItsCorrespondingRequest(requestId);
                executorService.submit(job);
                LOGGER.info("Submitted a new job for request n° " + requestId);
            } catch (Exception e) {
                LOGGER.error("Unable to create a new job for request n° " + requestId, e);
            }
        }
    }

    private DefaultJob createJob(int id, int requestId, String parameters) throws Exception {
        JobDefinition jobDefinition = jobDefinitions.get(id);
        Class<?> jobClass = Class.forName(jobDefinition.getClazz(), false, jobClassLoader);
        Object jobInstance = jobClass.newInstance();
        Map<String, String> parsedParameters = Utils.parseParameters(parameters);
        for (Map.Entry<String, String> entry : parsedParameters.entrySet()) {
            BeanUtils.setProperty(jobInstance, entry.getKey(), entry.getValue());
        }
        Method method = jobClass.getMethod(jobDefinition.getMethod());
        return new DefaultJob(requestId, jobInstance, method, this.jobService);
    }
}
