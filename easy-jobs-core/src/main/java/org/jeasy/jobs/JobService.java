package org.jeasy.jobs;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central service providing transactional methods to save/update job requests/executions together in a consistent way.
 */
@Service
class JobService {

    private static final Logger LOGGER = Logger.getLogger(JobService.class.getName());

    private JobExecutionDAO jobExecutionDAO;
    private JobRequestDAO jobRequestDAO;
    private ExecutorService executorService;
    private JobFactory jobFactory;

    JobService(ExecutorService executorService, JobExecutionDAO jobExecutionDAO, JobRequestDAO jobRequestDAO, JobFactory jobFactory) {
        this.jobExecutionDAO = jobExecutionDAO;
        this.jobRequestDAO = jobRequestDAO;
        this.executorService = executorService;
        this.jobFactory = jobFactory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveJobExecutionAndUpdateItsCorrespondingRequest(int requestId) {
        JobExecution jobExecution = new JobExecution(requestId, JobExecutionStatus.RUNNING, null, LocalDateTime.now(), null); // TODO constructor with less params or builder
        jobExecutionDAO.save(jobExecution);
        jobRequestDAO.updateStatus(requestId, JobRequestStatus.SUBMITTED);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void updateJobExecutionAndItsCorrespondingRequest(int requestId, JobExitStatus jobExitStatus) {
        jobExecutionDAO.update(requestId, jobExitStatus, LocalDateTime.now());
        jobRequestDAO.updateStatusAndProcessingDate(requestId, JobRequestStatus.PROCESSED, LocalDateTime.now());
    }

    // TODO 1. add transaction around this and you get horizontal scaling
    // The transaction will not wait for jobs to finish, only their submission (which should be fast enough)
    // TODO 2. what if new job request comes in in between? Well don't worry, it will be processed in the next run
    @Transactional
    void pollRequestsAndSubmitJobs() {
        System.out.println("JobService.pollRequestsAndSubmitJobs");
        List<JobRequest> pendingJobRequests = jobRequestDAO.getPendingJobRequests(); // add limit 10 (nb workers) and you have throttling/back pressure for free!
        if (pendingJobRequests.isEmpty()) {
            return;
        }
        LOGGER.info("Found " + pendingJobRequests.size() + " pending job request(s)");
        for (JobRequest pendingJobRequest : pendingJobRequests) {
            int requestId = pendingJobRequest.getId();
            int jobId = pendingJobRequest.getJobId();
            String parameters = pendingJobRequest.getParameters();
            // todo sanity check on jobId, if no job with given id, then warning + do nothing
            // todo add allowsConcurrent parameter: if there is already an execution for the job, don't create a new job and don't saveJobExecutionAndUpdateItsCorrespondingRequest, the request will be picked up in the next run
            LOGGER.info("Creating a new job for request n° " + requestId + " with parameters [" + parameters + "]");
            Callable<JobExitStatus> job;
            try {
                job = jobFactory.createJob(jobId, requestId, parameters);
                saveJobExecutionAndUpdateItsCorrespondingRequest(requestId);
                executorService.submit(job);
                LOGGER.info("Submitted a new job for request n° " + requestId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to create a new job for request n° " + requestId, e);
            }
        }
    }
}
