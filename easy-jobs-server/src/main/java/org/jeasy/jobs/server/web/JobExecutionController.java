package org.jeasy.jobs.server.web;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobExecutionController {

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @RequestMapping("/executions")
    List<JobExecution> getAllJobExecutions() {
        return jobExecutionRepository.findAllJobExecutions();
    }

}
