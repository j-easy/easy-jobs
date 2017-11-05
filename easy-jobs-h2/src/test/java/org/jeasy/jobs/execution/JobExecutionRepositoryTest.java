package org.jeasy.jobs.execution;

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

    @Test
    public void testFindAllJobExecutionsFromH2() throws Exception {
        super.testFindAllJobExecutions();
    }
}
