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

    public JobRequest(int id, int jobId, String parameters, JobRequestStatus status, LocalDateTime creationDate, LocalDateTime processingDate) {
        this.id = id;
        this.jobId = jobId;
        this.parameters = parameters;
        this.status = status;
        this.creationDate = creationDate;
        this.processingDate = processingDate;
    }

    public JobRequest(int jobId, String parameters, JobRequestStatus status, LocalDateTime creationDate, LocalDateTime processingDate) {
        this.jobId = jobId;
        this.parameters = parameters;
        this.status = status;
        this.creationDate = creationDate;
        this.processingDate = processingDate;
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
