package org.jeasy.jobs.server.web;

import org.jeasy.jobs.Utils;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobRequest;
import org.jeasy.jobs.request.JobRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class JobRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRequestController.class);

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
            return "You must at least provide the jobId for which you want to request an execution\n";
        }
        Map<String, String> parsedParameters = Utils.parseParameters(parameters);
        String jobId = parsedParameters.get("jobId");
        if (jobId == null) {
            return "jobId parameter is mandatory\n";
        }
        Integer jobIdentifier;
        try {
            jobIdentifier = Integer.parseInt(jobId);
        } catch (NumberFormatException e) {
            return "jobId parameter must be an integer\n";
        }
        if (jobRepository.findById(jobIdentifier) == null) {
            return "No job registered with id = " + jobId + "\n";
        }
        JobRequest jobRequest = new JobRequest(jobIdentifier, parameters);
        jobRequestRepository.save(jobRequest);
        LOGGER.info("Received a new job request for job " + jobId + " with parameters " + parsedParameters);
        return "Job request submitted successfully\n";
    }

}
