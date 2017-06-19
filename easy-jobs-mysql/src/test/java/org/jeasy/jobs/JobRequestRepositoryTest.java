package org.jeasy.jobs;

import org.junit.Test;

public class JobRequestRepositoryTest extends AbstractJobRequestRepositoryTest {

    @Test
    public void testJobRequestPersistenceInMySQL() throws Exception {
        super.testJobRequestPersistence();
    }

    @Test
    public void testGetPendingJobRequestsFromMySQL() throws Exception {
        super.testGetPendingJobRequests();
    }

    @Test
    public void testUpdateJobRequestStatusInMySQL() throws Exception {
        super.testUpdateJobRequestStatus();
    }

    @Test
    public void testUpdateJobRequestStatusAndProcessingDateInMySQL() throws Exception {
        super.testUpdateJobRequestStatusAndProcessingDate();
    }
}
