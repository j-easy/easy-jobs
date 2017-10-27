package org.jeasy.jobs.request;

import java.time.LocalDateTime;

public class JobExecutionRequest {

    private int id;
    private int jobId;
    private String parameters;
    // TODO add priotity
    private JobExecutionRequestStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime processingDate;

    public JobExecutionRequest() {
    }

    public JobExecutionRequest(int jobId, String parameters) {
        this.jobId = jobId;
        this.parameters = parameters;
        this.status = JobExecutionRequestStatus.PENDING;
        this.creationDate = LocalDateTime.now();
    }

    public JobExecutionRequest(int jobId, String parameters, JobExecutionRequestStatus status, LocalDateTime creationDate, LocalDateTime processingDate) {
        this.jobId = jobId;
        this.parameters = parameters;
        this.status = status;
        this.creationDate = creationDate;
        this.processingDate = processingDate;
    }

    public static JobExecutionRequest newJobExecutionRequest() {
        return new JobExecutionRequest();
    }

    public int getId() {
        return id;
    }

    public int getJobId() {
        return jobId;
    }

    public String getParameters() {
        return parameters;
    }

    public JobExecutionRequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getProcessingDate() {
        return processingDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public void setStatus(JobExecutionRequestStatus status) {
        this.status = status;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setProcessingDate(LocalDateTime processingDate) {
        this.processingDate = processingDate;
    }

    public JobExecutionRequest withJobId(int jobId) {
        this.jobId = jobId;
        return this;
    }

    public JobExecutionRequest withParameters(String parameters) {
        this.parameters = parameters;
        return this;
    }

    public JobExecutionRequest withStatus(JobExecutionRequestStatus status) {
        this.status = status;
        return this;
    }

    public JobExecutionRequest withCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public JobExecutionRequest withProcessingDate(LocalDateTime processingDate) {
        this.processingDate = processingDate;
        return this;
    }

    @Override
    public String toString() {
        return "JobExecutionRequest{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", parameters='" + parameters + '\'' +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", processingDate=" + processingDate +
                '}';
    }
}
