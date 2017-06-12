package org.jeasy.jobs;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

@Component
class JobFactory {

    private static final Logger LOGGER = Logger.getLogger(JobFactory.class.getName());

    Map<Integer, JobDefinition> jobs = new HashMap<>();

    private JobService jobService;

    JobFactory() {
    }

    DefaultJob createJob(int id, int requestId, String parameters) throws Exception {
        JobDefinition jobDefinition = jobs.get(id);
        String jobClass = jobDefinition.getClazz();
        String jobMethod = jobDefinition.getMethod();
        return createJob(requestId, jobClass, jobMethod, parameters);
    }

    private DefaultJob createJob(int requestId, String jobType, String jobMethod, String parameters) throws Exception {
        Class<?> jobClass = Class.forName(jobType);
        Object jobInstance = jobClass.newInstance();
        Map<String, Object> parsedParameters = parseParameters(parameters);
        for (Map.Entry<String, Object> entry : parsedParameters.entrySet()) {
            BeanUtils.setProperty(jobInstance, entry.getKey(), entry.getValue());
        }
        Method method = jobClass.getMethod(jobMethod);
        return new DefaultJob(requestId, jobInstance, method, jobService);
    }

    // fixme better use json
    private Map<String, Object> parseParameters(String parameters) {
        Map<String, Object> parsedParameters = new HashMap<>();
        String[] tokens = parameters.split(",");
        for (String token : tokens) {
            if( token.contains("=")) {
                String[] pair = token.split("=");
                String key = pair[0];
                String value = pair[1];
                parsedParameters.put(key, value);
            } else {
                LOGGER.warning("Parameter '" + token + "' not in 'key=value' format");
            }
        }
        return parsedParameters;
    }

    void setJobService(JobService jobService) {
        this.jobService = jobService;
    }

    public void setJobs(Map<Integer, JobDefinition> jobs) {
        this.jobs = jobs;
    }
}
