package org.jeasy.jobs.request;

import org.junit.Test;

public class JobExecutionRequestRepositoryTest extends AbstractJobExecutionRequestRepositoryTest {

    @Test
    public void testJobExecutionRequestPersistenceInH2() throws Exception {
        super.testJobExecutionRequestPersistence();
    }

    @Test
    public void testFindJobExecutionRequestsByStatusFromH2() throws Exception {
        super.testFindJobExecutionRequestsByStatus();
    }

    @Test
    public void testUpdateJobExecutionRequestInH2() throws Exception {
        super.testUpdateJobExecutionRequest();
    }

    @Test
    public void testFindAllJobExecutionRequestsFromH2() throws Exception {
        super.testFindAllJobExecutionRequests();
    }
}
