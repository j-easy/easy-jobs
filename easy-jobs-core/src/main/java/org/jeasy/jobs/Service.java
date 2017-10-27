package org.jeasy.jobs;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.execution.JobExecutionStatus;
import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.jeasy.jobs.request.JobExecutionRequestRepository;
import org.jeasy.jobs.request.JobExecutionRequestStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.jeasy.jobs.execution.JobExecution.newJobExecution;

/**
 * Central service providing transactional methods to save/update job requests/executions together in a consistent way.
 */
@org.springframework.stereotype.Service
@Transactional
public class Service {

    private JobRepository jobRepository;
    private JobExecutionRepository jobExecutionRepository;
    private JobExecutionRequestRepository jobExecutionRequestRepository;

    public Service(JobExecutionRepository jobExecutionRepository, JobExecutionRequestRepository jobExecutionRequestRepository, JobRepository jobRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobExecutionRequestRepository = jobExecutionRequestRepository;
        this.jobRepository = jobRepository;
    }

    public void saveJobExecutionAndUpdateItsCorrespondingRequest(int requestId) {
        JobExecution jobExecution = newJobExecution().withRequestId(requestId).withJobExecutionStatus(JobExecutionStatus.RUNNING).withStartDate(LocalDateTime.now());
        jobExecutionRepository.save(jobExecution);
        JobExecutionRequest jobExecutionRequest = jobExecutionRequestRepository.findJobExecutionRequestById(requestId);
        jobExecutionRequest.setStatus(JobExecutionRequestStatus.SUBMITTED);
        jobExecutionRequestRepository.update(jobExecutionRequest);
    }

    public void updateJobExecutionAndItsCorrespondingRequest(int requestId, JobExitStatus jobExitStatus) {
        JobExecution jobExecution = jobExecutionRepository.findByJobExecutionRequestId(requestId);
        jobExecution.setJobExecutionStatus(JobExecutionStatus.FINISHED);
        jobExecution.setJobExitStatus(jobExitStatus);
        jobExecution.setEndDate(LocalDateTime.now());
        jobExecutionRepository.update(jobExecution);

        JobExecutionRequest jobExecutionRequest = jobExecutionRequestRepository.findJobExecutionRequestById(requestId);
        jobExecutionRequest.setStatus(JobExecutionRequestStatus.PROCESSED);
        jobExecutionRequest.setProcessingDate(LocalDateTime.now());
        jobExecutionRequestRepository.update(jobExecutionRequest);
    }

    @Transactional(readOnly = true)
    public Job findJobById(int jobId) {
        return jobRepository.findById(jobId);
    }

    @Transactional(readOnly = true)
    public List<JobExecutionRequest> findPendingRequests() {
        return jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PENDING);
    }

}
