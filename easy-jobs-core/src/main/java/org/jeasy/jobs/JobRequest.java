package org.jeasy.jobs;

import java.time.LocalDateTime;

class JobRequest {

    private int id;
    private int jobId;
    private String parameters;
    // TODO add priotity
    private JobRequestStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime processingDate;

    public JobRequest(int id, int jobId, String parameters, JobRequestStatus status, LocalDateTime creationDate, LocalDateTime processingDate) {
        this.id = id;
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
}
