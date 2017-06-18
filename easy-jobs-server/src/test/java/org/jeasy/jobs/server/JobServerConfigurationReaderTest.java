package org.jeasy.jobs.server;

import org.assertj.core.api.Assertions;
import org.jeasy.jobs.job.JobDefinition;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JobServerConfigurationReaderTest {

    private JobServerConfigurationReader reader = new JobServerConfigurationReader();

    @Test
    public void testConfigurationParsing() throws Exception {
        // Given
        File file = new File("src/test/resources/easy-jobs-config.json");

        // When
        JobServerConfiguration serverConfiguration = reader.read(file);

        // Then
        assertThat(serverConfiguration.getWorkersNumber()).isEqualTo(10);
        assertThat(serverConfiguration.getPollingInterval()).isEqualTo(30);
        assertThat(serverConfiguration.isDatabaseInit()).isTrue();

        List<JobDefinition> jobDefinitions = serverConfiguration.getJobDefinitions();
        Assertions.assertThat(jobDefinitions).isNotEmpty();

        JobDefinition jobDefinition = jobDefinitions.get(0);
        assertThat(jobDefinition.getId()).isEqualTo(1);
        assertThat(jobDefinition.getName()).isEqualTo("my job");
        assertThat(jobDefinition.getClazz()).isEqualTo("org.jeasy.jobs.test.MyJob");
        assertThat(jobDefinition.getMethod()).isEqualTo("doWork");
    }
}