package org.jeasy.jobs.server.web;

import org.jeasy.jobs.Utils;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.jeasy.jobs.request.JobExecutionRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class JobExecutionRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionRequestController.class);

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobExecutionRequestRepository jobExecutionRequestRepository;

    @RequestMapping("/requests/{id}")
    JobExecutionRequest getJobExecutionRequest(@PathVariable int id) {
        return jobExecutionRequestRepository.findJobExecutionRequestById(id);
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    List<JobExecutionRequest> getAllJobExecutionRequests() {
        return jobExecutionRequestRepository.findAllJobExecutionRequests();
    }

    @RequestMapping(path = "/requests", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseBody
    String postJobExecutionRequest(@RequestBody String parameters) throws Exception {
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
        JobExecutionRequest jobExecutionRequest = new JobExecutionRequest(jobIdentifier, parameters);
        jobExecutionRequestRepository.save(jobExecutionRequest);
        LOGGER.info("Received a new job execution request for job " + jobId + " with parameters " + parsedParameters);
        return "Job execution request submitted successfully\n";
    }

}
