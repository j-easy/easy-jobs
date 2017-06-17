package org.jeasy.jobs.execution;

import org.jeasy.jobs.job.JobExitStatus;

import java.time.LocalDateTime;

public class JobExecution {

    private int id;
    private int requestId;
    private JobExecutionStatus jobExecutionStatus;
    private JobExitStatus jobExitStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public JobExecution() {
    }

    public JobExecution(int requestId, JobExecutionStatus jobExecutionStatus, JobExitStatus jobExitStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.requestId = requestId;
        this.jobExecutionStatus = jobExecutionStatus;
        this.jobExitStatus = jobExitStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public int getRequestId() {
        return requestId;
    }

    public JobExecutionStatus getJobExecutionStatus() {
        return jobExecutionStatus;
    }

    public JobExitStatus getJobExitStatus() {
        return jobExitStatus;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setJobExecutionStatus(JobExecutionStatus jobExecutionStatus) {
        this.jobExecutionStatus = jobExecutionStatus;
    }

    public void setJobExitStatus(JobExitStatus jobExitStatus) {
        this.jobExitStatus = jobExitStatus;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
