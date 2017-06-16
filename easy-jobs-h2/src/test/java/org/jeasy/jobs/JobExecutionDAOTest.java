package org.jeasy.jobs;

import org.junit.Test;

public class JobExecutionDAOTest extends AbstractJobExecutionDAOTest {

    @Test
    public void testJobExecutionPersistenceInH2() throws Exception {
        super.testJobExecutionPersistence();
    }
}
