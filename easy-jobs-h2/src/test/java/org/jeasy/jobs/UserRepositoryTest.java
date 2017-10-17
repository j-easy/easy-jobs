package org.jeasy.jobs;

import org.junit.Test;

public class UserRepositoryTest extends AbstractUserRepositoryTest {

    @Test
    public void testGetUserFromH2() throws Exception {
        super.testGetUserByNameAndPassword();
    }

}
