package org.jeasy.jobs.job;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class JobRepository {

    private SessionFactory sessionFactory;

    public JobRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Job job) {
        sessionFactory.getCurrentSession().saveOrUpdate(job);
    }

    public List<Job> findAll() {
        Query<Job> query = sessionFactory.getCurrentSession().createQuery("from Job", Job.class);
        return query.list();
    }

    public Job findById(int id) {
        return sessionFactory.getCurrentSession().get(Job.class, id);
    }

}
