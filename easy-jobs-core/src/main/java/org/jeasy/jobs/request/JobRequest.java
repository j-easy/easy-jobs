package org.jeasy.jobs.request;

import java.time.LocalDateTime;

public class JobRequest {

    private int id;
    private int jobId;
    private String parameters;
    // TODO add priotity
    private JobRequestStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime processingDate;

    public JobRequest() {
    }

    public JobRequest(int jobId, String parameters) {
        this.jobId = jobId;
        this.parameters = parameters;
        this.status = JobRequestStatus.PENDING;
        this.creationDate = LocalDateTime.now();
    }

    public JobRequest(int jobId, String parameters, JobRequestStatus status, LocalDateTime creationDate, LocalDateTime processingDate) {
        this.jobId = jobId;
        this.parameters = parameters;
        this.status = status;
        this.creationDate = creationDate;
        this.processingDate = processingDate;
    }

    public static JobRequest newJobRequest() {
        return new JobRequest();
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

    public JobRequestStatus getStatus() {
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

    public void setStatus(JobRequestStatus status) {
        this.status = status;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setProcessingDate(LocalDateTime processingDate) {
        this.processingDate = processingDate;
    }

    public JobRequest withJobId(int jobId) {
        this.jobId = jobId;
        return this;
    }

    public JobRequest withParameters(String parameters) {
        this.parameters = parameters;
        return this;
    }

    public JobRequest withStatus(JobRequestStatus status) {
        this.status = status;
        return this;
    }

    public JobRequest withCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public JobRequest withProcessingDate(LocalDateTime processingDate) {
        this.processingDate = processingDate;
        return this;
    }

    @Override
    public String toString() {
        return "JobRequest{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", parameters='" + parameters + '\'' +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", processingDate=" + processingDate +
                '}';
    }
}
