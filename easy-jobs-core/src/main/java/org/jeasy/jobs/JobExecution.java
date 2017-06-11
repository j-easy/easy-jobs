package org.jeasy.jobs;

import java.time.LocalDateTime;

class JobExecution {

    private int id;
    private int requestId;
    private JobExecutionStatus jobExecutionStatus;
    private JobExitStatus jobExitStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

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
}
