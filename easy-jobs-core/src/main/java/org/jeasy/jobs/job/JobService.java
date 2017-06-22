package org.jeasy.jobs.job;

import org.apache.commons.beanutils.BeanUtils;
import org.jeasy.jobs.Utils;
import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.execution.JobExecutionStatus;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestRepository;
import org.jeasy.jobs.request.JobRequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jeasy.jobs.execution.JobExecution.newJobExecution;

/**
 * Central service providing transactional methods to save/update job requests/executions together in a consistent way.
 */
@Service
public class JobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

    private JobRepository jobRepository;
    private JobExecutionRepository jobExecutionDAO;
    private JobRequestRepository jobRequestRepository;
    private ExecutorService executorService;
    private Map<Integer, JobDefinition> jobDefinitions;

    public JobService(JobExecutionRepository jobExecutionDAO, JobRequestRepository jobRequestRepository, JobRepository jobRepository) {
        this.jobExecutionDAO = jobExecutionDAO;
        this.jobRequestRepository = jobRequestRepository;
        this.jobRepository = jobRepository;
        this.executorService = Executors.newSingleThreadExecutor();
        this.jobDefinitions = new HashMap<>();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveJobExecutionAndUpdateItsCorrespondingRequest(int requestId) {
        JobExecution jobExecution = newJobExecution().withRequestId(requestId).withJobExecutionStatus(JobExecutionStatus.RUNNING).withStartDate(LocalDateTime.now());
        jobExecutionDAO.save(jobExecution);
        JobRequest jobRequest = jobRequestRepository.findById(requestId);
        jobRequest.setStatus(JobRequestStatus.SUBMITTED);
        jobRequestRepository.update(jobRequest);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateJobExecutionAndItsCorrespondingRequest(int requestId, JobExitStatus jobExitStatus) {
        JobExecution jobExecution = jobExecutionDAO.findByJobRequestId(requestId);
        jobExecution.setJobExecutionStatus(JobExecutionStatus.FINISHED);
        jobExecution.setJobExitStatus(jobExitStatus);
        jobExecution.setEndDate(LocalDateTime.now());
        jobExecutionDAO.update(jobExecution);

        JobRequest jobRequest = jobRequestRepository.findById(requestId);
        jobRequest.setStatus(JobRequestStatus.PROCESSED);
        jobRequest.setProcessingDate(LocalDateTime.now());
        jobRequestRepository.update(jobRequest);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void pollRequestsAndSubmitJobs() {
        List<JobRequest> pendingJobRequests = jobRequestRepository.findJobRequestsByStatus(JobRequestStatus.PENDING); // add limit 10 (nb workers) and you have throttling/back pressure for free!
        if (pendingJobRequests.isEmpty()) {
            return;
        }
        LOGGER.info("Found " + pendingJobRequests.size() + " pending job request(s)");
        for (JobRequest pendingJobRequest : pendingJobRequests) {
            int requestId = pendingJobRequest.getId();
            int jobId = pendingJobRequest.getJobId();
            String parameters = pendingJobRequest.getParameters();
            if (jobRepository.findById(jobId) == null) { // this is an extra check on the core side, the web controller already filters bad requests upfront
                LOGGER.warn("Unable to find a job with id " + jobId + ". Ignoring request with id " + requestId);
                continue;
            }
            // todo add allowsConcurrent parameter: if there is already an execution for the job, don't create a new job and don't saveJobExecutionAndUpdateItsCorrespondingRequest, the request will be picked up in the next run
            LOGGER.info("Creating a new job for request n° " + requestId + " with parameters [" + parameters + "]");
            Callable<JobExitStatus> job;
            try {
                job = createJob(jobId, requestId, parameters);
                saveJobExecutionAndUpdateItsCorrespondingRequest(requestId);
                executorService.submit(job);
                LOGGER.info("Submitted a new job for request n° " + requestId);
            } catch (Exception e) {
                LOGGER.error("Unable to create a new job for request n° " + requestId, e);
            }
        }
    }

    private DefaultJob createJob(int id, int requestId, String parameters) throws Exception {
        JobDefinition jobDefinition = jobDefinitions.get(id);
        Class<?> jobClass = Class.forName(jobDefinition.getClazz());
        Object jobInstance = jobClass.newInstance();
        Map<String, String> parsedParameters = Utils.parseParameters(parameters);
        for (Map.Entry<String, String> entry : parsedParameters.entrySet()) {
            BeanUtils.setProperty(jobInstance, entry.getKey(), entry.getValue());
        }
        Method method = jobClass.getMethod(jobDefinition.getMethod());
        return new DefaultJob(requestId, jobInstance, method, this);
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setJobDefinitions(Map<Integer, JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }
}
