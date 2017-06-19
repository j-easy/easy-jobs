package org.jeasy.jobs;

import org.junit.Test;

public class JobExecutionRepositoryTest extends AbstractJobExecutionRepositoryTest {

    @Test
    public void testJobExecutionPersistenceInMySQL() throws Exception {
        super.testJobExecutionPersistence();
    }

    @Test
    public void testJobExecutionUpdateInMySQL() throws Exception {
        super.testJobExecutionUpdate();
    }
}
