package org.jeasy.jobs.server.web;

import org.jeasy.jobs.job.Job;
import org.jeasy.jobs.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @RequestMapping("/jobs")
    List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

}
