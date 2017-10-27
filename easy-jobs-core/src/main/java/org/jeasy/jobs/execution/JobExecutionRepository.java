package org.jeasy.jobs.execution;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class JobExecutionRepository {

    private SessionFactory sessionFactory;

    public JobExecutionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public JobExecution findByJobExecutionRequestId(int jobExecutionRequestId) {
        Session session = sessionFactory.getCurrentSession();
        Query<JobExecution> query = session.createQuery("from JobExecution where requestId = :requestId ", JobExecution.class);
        query.setParameter("requestId", jobExecutionRequestId);
        return query.getSingleResult();
    }

    public List<JobExecution> findAllJobExecutions() {
        Session session = sessionFactory.getCurrentSession();
        Query<JobExecution> query = session.createQuery("from JobExecution", JobExecution.class);
        return query.list();
    }

    public void save(JobExecution jobExecution) {
        sessionFactory.getCurrentSession().save(jobExecution);
    }

    public void update(JobExecution jobExecution) {
        sessionFactory.getCurrentSession().update(jobExecution);
    }

}
