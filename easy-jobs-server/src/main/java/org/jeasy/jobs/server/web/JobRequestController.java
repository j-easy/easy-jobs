package org.jeasy.jobs.server.web;

import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobRequestController {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobRequestRepository jobRequestRepository;

    @RequestMapping("/requests/{id}")
    JobRequest getJobRequest(@PathVariable int id) {
        return jobRequestRepository.getById(id);
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    List<JobRequest> getAllJobRequests() {
        return jobRequestRepository.findAllJobRequests();
    }

    @RequestMapping(path = "/requests", method = RequestMethod.POST)
    @ResponseBody
    String postJobRequest(@RequestParam int jobId, @RequestParam(required = false) String parameters) {
        if (jobRepository.getById(jobId) == null) {
            return "No job registered with id = " + jobId;
        }
        JobRequest jobRequest = new JobRequest(jobId, parameters == null ? "" : parameters);
        jobRequestRepository.save(jobRequest);
        return "Job request submitted successfully";
    }

}
