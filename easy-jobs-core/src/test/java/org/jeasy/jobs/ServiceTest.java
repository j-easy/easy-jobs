package org.jeasy.jobs;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.jeasy.jobs.request.JobExecutionRequestRepository;
import org.jeasy.jobs.request.JobExecutionRequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * TODO Remove all mocks and test on a real database
 */

@RunWith(MockitoJUnitRunner.class)
public class ServiceTest {

    private Service service;

    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobExecutionRepository jobExecutionRepository;
    @Mock
    private JobExecutionRequestRepository jobExecutionRequestRepository;
    @Mock
    private JobExecutionRequest jobExecutionRequest;
    @Mock
    private JobExecution jobExecution;

    @Before
    public void setUp() throws Exception {
        service = new Service(jobExecutionRepository, jobExecutionRequestRepository, jobRepository);
    }

    @Test
    public void saveJobExecutionAndUpdateItsCorrespondingRequest() {
        // given
        int requestId = 1;
        when(jobExecutionRequestRepository.findJobExecutionRequestById(requestId)).thenReturn(jobExecutionRequest);

        // when
        service.saveJobExecutionAndUpdateItsCorrespondingRequest(requestId);

        // then
        verify(jobExecutionRepository).save(any(JobExecution.class));
        verify(jobExecutionRequestRepository).update(jobExecutionRequest);
    }

    @Test
    public void updateJobExecutionAndItsCorrespondingRequest() {
        // given
        int requestId = 1;
        when(jobExecutionRepository.findByJobExecutionRequestId(requestId)).thenReturn(jobExecution);
        when(jobExecutionRequestRepository.findJobExecutionRequestById(requestId)).thenReturn(jobExecutionRequest);

        // when
        service.updateJobExecutionAndItsCorrespondingRequest(requestId, JobExitStatus.SUCCEEDED);

        // then
        verify(jobExecutionRepository).update(jobExecution);
        verify(jobExecutionRequestRepository).update(jobExecutionRequest);
    }

    @Test
    public void updateJobExecutionRequestStatus() {
        // given
        int requestId = 1;
        when(jobExecutionRequestRepository.findJobExecutionRequestById(requestId)).thenReturn(jobExecutionRequest);

        // when
        service.updateJobExecutionRequestStatus(requestId, JobExecutionRequestStatus.PENDING);

        // then
        verify(jobExecutionRequestRepository).update(jobExecutionRequest);
    }

    @Test
    public void findJobById() {
        // given
        int jobId = 1;

        // when
        service.findJobById(jobId);

        // then
        verify(jobRepository).findById(jobId);
    }

    @Test
    public void findPendingRequests() {
        // when
        service.findPendingRequests();

        // then
        verify(jobExecutionRequestRepository).findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PENDING);
    }

}