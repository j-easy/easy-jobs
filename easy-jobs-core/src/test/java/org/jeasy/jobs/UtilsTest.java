package org.jeasy.jobs;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    @Test
    public void parseParameters() throws Exception {
        // given
        String parameters = "{\"jobId\":\"1\", \"name\":\"world\"}";

        // when
        Map<String, String> parsedParameters = Utils.parseParameters(parameters);

        // then
        assertThat(parsedParameters).isNotNull();
        assertThat(parsedParameters.get("jobId")).isEqualTo("1");
        assertThat(parsedParameters.get("name")).isEqualTo("world");
    }

}