package org.jeasy.jobs.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.jobs.job.JobDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobDefinitions {

    static final String JOBS_DEFINITIONS_CONFIGURATION_FILE_PARAMETER_NAME = "easy.jobs.server.jobs.config.file";

    private String sourceFile;
    private List<JobDefinition> jobDefinitions;

    public JobDefinitions(List<JobDefinition> jobDefinitions, String sourceFile) {
        this.jobDefinitions = jobDefinitions;
        this.sourceFile = sourceFile;
    }

    public List<JobDefinition> getJobDefinitions() {
        return jobDefinitions;
    }

    public void setJobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    static class Reader {

        private ObjectMapper mapper = new ObjectMapper();

        JobDefinitions read(File file) throws Exception {
            List<JobDefinition> jobDefinitions = Arrays.asList(mapper.readValue(file, JobDefinition[].class));
            return new JobDefinitions(jobDefinitions, file.getAbsolutePath());
        }

    }

    Map<Integer, JobDefinition> mapJobDefinitionsToJobIdentifiers() {
        Map<Integer, JobDefinition> jobDefinitionsMap = new HashMap<>();
        for (JobDefinition jobDefinition : jobDefinitions) {
            jobDefinitionsMap.put(jobDefinition.getId(), jobDefinition);
        }
        return jobDefinitionsMap;
    }

    static String getSimpleNameFrom(String fullyQualifiedName) {
        int beginIndex = fullyQualifiedName.lastIndexOf('.');
        if(beginIndex == -1) {
            return fullyQualifiedName;
        }
        return fullyQualifiedName.substring(beginIndex + 1);
    }
}
