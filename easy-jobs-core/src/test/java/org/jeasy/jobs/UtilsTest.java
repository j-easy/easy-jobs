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

    @Test
    public void parseEmptyParameters() throws Exception {
        // given
        String parameters = "";

        // when
        Map<String, String> parsedParameters = Utils.parseParameters(parameters);

        // then
        assertThat(parsedParameters).isNotNull();
    }

    @Test
    public void md5() throws Exception {
        // given
        String input = "admin";

        // when
        String actual = Utils.md5(input);

        // then
        assertThat(actual).isEqualTo("21232f297a57a5a743894a0e4a801fc3");
    }

}