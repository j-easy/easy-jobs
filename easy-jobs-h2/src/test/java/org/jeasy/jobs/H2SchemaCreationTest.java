package org.jeasy.jobs;

import org.junit.Test;

public class H2SchemaCreationTest extends SchemaCreationTest {

    @Test
    public void canExecuteSQLScriptAgainstH2Database() throws Exception {
        super.canExecuteSQLScript();
    }

}