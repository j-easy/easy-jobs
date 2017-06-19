package org.jeasy.jobs.job;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JobRepository { // todo rename all dao to repository

    private SessionFactory sessionFactory;

    public JobRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Job job) {
        sessionFactory.getCurrentSession().saveOrUpdate(job);
    }

}
