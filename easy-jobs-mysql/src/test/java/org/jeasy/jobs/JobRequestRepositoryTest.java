package org.jeasy.jobs;

import org.junit.Test;

public class JobRequestRepositoryTest extends AbstractJobRequestRepositoryTest {

    @Test
    public void testJobRequestPersistenceInMySQL() throws Exception {
        super.testJobRequestPersistence();
    }

    @Test
    public void testFindJobRequestsByStatusFromMySQL() throws Exception {
        super.testFindJobRequestsByStatus();
    }

    @Test
    public void testUpdateJobRequestInMySQL() throws Exception {
        super.testUpdateJobRequest();
    }

    @Test
    public void testFindAllJobRequestsFromMySQL() throws Exception {
        super.testFindAllJobRequests();
    }
}
