package org.jeasy.jobs.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.jobs.job.JobDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class JobDefinitions {

    static final String JOBS_DEFINITIONS_CONFIGURATION_FILE_PARAMETER_NAME = "easy.jobs.server.jobs.config.file";

    private List<JobDefinition> jobDefinitions;

    public JobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    public List<JobDefinition> getJobDefinitions() {
        return jobDefinitions;
    }

    public void setJobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    static class Reader {

        private ObjectMapper mapper = new ObjectMapper();

        JobDefinitions read(File file) throws Exception {
            List<JobDefinition> jobDefinitions = Arrays.asList(mapper.readValue(file, JobDefinition[].class));
            return new JobDefinitions(jobDefinitions);
        }

    }
}
