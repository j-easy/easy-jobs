package org.jeasy.jobs.server;

import org.jeasy.jobs.job.JobService;

public class JobRequestPoller implements Runnable {

    private JobService jobService;

    public JobRequestPoller(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public void run() {
        jobService.pollRequestsAndSubmitJobs();
    }
}
