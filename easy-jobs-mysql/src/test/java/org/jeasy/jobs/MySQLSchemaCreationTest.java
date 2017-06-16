package org.jeasy.jobs;

import org.junit.Test;

public class MySQLSchemaCreationTest extends SchemaCreationTest {

    @Test
    public void canExecuteSQLScriptAgainstMySQLDatabase() throws Exception {
        super.canExecuteSQLScript();
    }

}