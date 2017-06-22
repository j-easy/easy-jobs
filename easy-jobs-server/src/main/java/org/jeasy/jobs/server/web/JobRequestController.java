package org.jeasy.jobs.server.web;

import org.jeasy.jobs.Utils;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class JobRequestController {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobRequestRepository jobRequestRepository;

    @RequestMapping("/requests/{id}")
    JobRequest getJobRequest(@PathVariable int id) {
        return jobRequestRepository.findById(id);
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    List<JobRequest> getAllJobRequests() {
        return jobRequestRepository.findAllJobRequests();
    }

    @RequestMapping(path = "/requests", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseBody
    String postJobRequest(@RequestBody String parameters) throws Exception {
        if (parameters == null || parameters.isEmpty()) {
            return "You must at least provide the jobId for which you want to request an execution";
        }
        Map<String, String> parsedParameters = Utils.parseParameters(parameters);
        String jobId = parsedParameters.get("jobId");
        if (jobId == null) {
            return "jobId parameter is mandatory";
        }
        Integer jobIdentifier;
        try {
            jobIdentifier = Integer.parseInt(jobId);
        } catch (NumberFormatException e) {
            return "jobId parameter must be an integer";
        }
        if (jobRepository.findById(jobIdentifier) == null) {
            return "No job registered with id = " + jobId;
        }
        JobRequest jobRequest = new JobRequest(jobIdentifier, parameters);
        jobRequestRepository.save(jobRequest);
        return "Job request submitted successfully";
    }

}
