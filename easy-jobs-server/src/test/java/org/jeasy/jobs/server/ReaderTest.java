package org.jeasy.jobs.server;

import org.assertj.core.api.Assertions;
import org.jeasy.jobs.job.JobDefinition;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReaderTest {

    private JobDefinitions.Reader reader = new JobDefinitions.Reader();

    @Test
    public void testMultipleJobDefinitionsParsing() throws Exception {
        // Given
        File file = new File("src/test/resources/jobs.yaml");

        // When
        JobDefinitions jobDefinitions = reader.read(file);

        // Then

        List<JobDefinition> actual = jobDefinitions.getJobDefinitions();
        Assertions.assertThat(actual).hasSize(2);

        JobDefinition jobDefinition = actual.get(0);
        assertThat(jobDefinition.getId()).isEqualTo(1);
        assertThat(jobDefinition.getName()).isEqualTo("MyFirstJob");
        assertThat(jobDefinition.getDescription()).isEqualTo("my first job");
        assertThat(jobDefinition.getClazz()).isEqualTo("org.mycompany.jobs.MyFirstJob");
        assertThat(jobDefinition.getMethod()).isEqualTo("doWork");

        jobDefinition = actual.get(1);
        assertThat(jobDefinition.getId()).isEqualTo(2);
        assertThat(jobDefinition.getName()).isEqualTo("MySecondJob");
        assertThat(jobDefinition.getDescription()).isEqualTo("my second job");
        assertThat(jobDefinition.getClazz()).isEqualTo("org.mycompany.jobs.MySecondJob");
        assertThat(jobDefinition.getMethod()).isEqualTo("doWork");
    }

    @Test
    public void testSingleJobDefinitionParsing() throws Exception {
        // Given
        File file = new File("src/test/resources/job.yaml");

        // When
        JobDefinitions jobDefinitions = reader.read(file);

        // Then

        List<JobDefinition> actual = jobDefinitions.getJobDefinitions();
        Assertions.assertThat(actual).hasSize(1);

        JobDefinition jobDefinition = actual.get(0);
        assertThat(jobDefinition.getId()).isEqualTo(1);
        assertThat(jobDefinition.getName()).isEqualTo("MyFirstJob");
        assertThat(jobDefinition.getDescription()).isEqualTo("my first job");
        assertThat(jobDefinition.getClazz()).isEqualTo("org.mycompany.jobs.MyFirstJob");
        assertThat(jobDefinition.getMethod()).isEqualTo("doWork");
    }
}