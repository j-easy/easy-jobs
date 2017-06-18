package org.jeasy.jobs.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
class JobServerConfigurationReader {

    private ObjectMapper mapper = new ObjectMapper();

    JobServerConfiguration read(File file) throws Exception {
        return mapper.readValue(file, JobServerConfiguration.class);
    }

}
