package org.jeasy.jobs;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.execution.JobExecutionStatus;
import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestRepository;
import org.jeasy.jobs.request.JobRequestStatus;
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
    private JobRequestRepository jobRequestRepository;

    public Service(JobExecutionRepository jobExecutionRepository, JobRequestRepository jobRequestRepository, JobRepository jobRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobRequestRepository = jobRequestRepository;
        this.jobRepository = jobRepository;
    }

    public void saveJobExecutionAndUpdateItsCorrespondingRequest(int requestId) {
        JobExecution jobExecution = newJobExecution().withRequestId(requestId).withJobExecutionStatus(JobExecutionStatus.RUNNING).withStartDate(LocalDateTime.now());
        jobExecutionRepository.save(jobExecution);
        JobRequest jobRequest = jobRequestRepository.findById(requestId);
        jobRequest.setStatus(JobRequestStatus.SUBMITTED);
        jobRequestRepository.update(jobRequest);
    }

    public void updateJobExecutionAndItsCorrespondingRequest(int requestId, JobExitStatus jobExitStatus) {
        JobExecution jobExecution = jobExecutionRepository.findByJobRequestId(requestId);
        jobExecution.setJobExecutionStatus(JobExecutionStatus.FINISHED);
        jobExecution.setJobExitStatus(jobExitStatus);
        jobExecution.setEndDate(LocalDateTime.now());
        jobExecutionRepository.update(jobExecution);

        JobRequest jobRequest = jobRequestRepository.findById(requestId);
        jobRequest.setStatus(JobRequestStatus.PROCESSED);
        jobRequest.setProcessingDate(LocalDateTime.now());
        jobRequestRepository.update(jobRequest);
    }

    @Transactional(readOnly = true)
    public Job findJobById(int jobId) {
        return jobRepository.findById(jobId);
    }

    @Transactional(readOnly = true)
    public List<JobRequest> findPendingRequests() {
        return jobRequestRepository.findJobRequestsByStatus(JobRequestStatus.PENDING);
    }

}
