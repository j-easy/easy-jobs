package org.jeasy.jobs;

import org.junit.Test;

public class JobRequestRepositoryTest extends AbstractJobRequestRepositoryTest {

    @Test
    public void testJobRequestPersistenceInH2() throws Exception {
        super.testJobRequestPersistence();
    }

    @Test
    public void testGetPendingJobRequestsFromH2() throws Exception {
        super.testGetPendingJobRequests();
    }

    @Test
    public void testUpdateJobRequestStatusInH2() throws Exception {
        super.testUpdateJobRequestStatus();
    }

    @Test
    public void testUpdateJobRequestStatusAndProcessingDateInH2() throws Exception {
        super.testUpdateJobRequestStatusAndProcessingDate();
    }

    @Test
    public void testFindAllJobRequestsFromH2() throws Exception {
        super.testFindAllJobRequests();
    }
}
