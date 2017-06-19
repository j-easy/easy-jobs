package org.jeasy.jobs;

import org.junit.Test;

public class JobExecutionRepositoryTest extends AbstractJobExecutionRepositoryTest {

    @Test
    public void testJobExecutionPersistenceInH2() throws Exception {
        super.testJobExecutionPersistence();
    }

    @Test
    public void testJobExecutionUpdateInH2() throws Exception {
        super.testJobExecutionUpdate();
    }
}
