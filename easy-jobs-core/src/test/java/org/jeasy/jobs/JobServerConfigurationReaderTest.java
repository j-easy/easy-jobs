package org.jeasy.jobs;

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
        assertThat(serverConfiguration.getDatabaseType()).isEqualTo("h2");
        assertThat(serverConfiguration.isDatabaseInit()).isTrue();

        List<JobServerConfiguration.JobDefinition> jobDefinitions = serverConfiguration.getJobDefinitions();
        assertThat(jobDefinitions).isNotEmpty();

        JobServerConfiguration.JobDefinition jobDefinition = jobDefinitions.get(0);
        assertThat(jobDefinition.getId()).isEqualTo(1);
        assertThat(jobDefinition.getName()).isEqualTo("my job");
        assertThat(jobDefinition.getClazz()).isEqualTo("org.jeasy.jobs.test.MyJob");
        assertThat(jobDefinition.getMethod()).isEqualTo("doWork");
    }
}