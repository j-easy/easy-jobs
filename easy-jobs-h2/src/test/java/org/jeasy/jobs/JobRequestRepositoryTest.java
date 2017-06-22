package org.jeasy.jobs;

import org.junit.Test;

public class JobRequestRepositoryTest extends AbstractJobRequestRepositoryTest {

    @Test
    public void testJobRequestPersistenceInH2() throws Exception {
        super.testJobRequestPersistence();
    }

    @Test
    public void testFindJobRequestsByStatusFromH2() throws Exception {
        super.testFindJobRequestsByStatus();
    }

    @Test
    public void testUpdateJobRequestInH2() throws Exception {
        super.testUpdateJobRequest();
    }

    @Test
    public void testFindAllJobRequestsFromH2() throws Exception {
        super.testFindAllJobRequests();
    }
}
