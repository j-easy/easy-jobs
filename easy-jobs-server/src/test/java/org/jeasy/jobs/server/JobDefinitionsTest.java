package org.jeasy.jobs.server;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JobDefinitionsTest {

    @Test
    public void testGetSimpleName() throws Exception {
        String simpleName = JobDefinitions.getSimpleNameFrom("org.mycompany.jobs.MyFirstJob");
        assertThat(simpleName).isEqualTo("MyFirstJob");

        simpleName = JobDefinitions.getSimpleNameFrom("MySecondJob");
        assertThat(simpleName).isEqualTo("MySecondJob");
    }

}