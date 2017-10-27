package org.jeasy.jobs.request;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class JobExecutionRequestRepository {

    private SessionFactory sessionFactory;

    public JobExecutionRequestRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public JobExecutionRequest findJobExecutionRequestById(int id) {
        return sessionFactory.getCurrentSession().get(JobExecutionRequest.class, id);
    }

    public void save(JobExecutionRequest jobExecutionRequest) {
        sessionFactory.getCurrentSession().save(jobExecutionRequest);
    }

    public void update(JobExecutionRequest jobExecutionRequest) {
        sessionFactory.getCurrentSession().update(jobExecutionRequest);
    }

    public List<JobExecutionRequest> findJobExecutionRequestsByStatus(JobExecutionRequestStatus status) {
        // TODO order by priority
        Session session = sessionFactory.getCurrentSession();
        Query<JobExecutionRequest> query = session.createQuery("from JobExecutionRequest where status = :status ", JobExecutionRequest.class);
        query.setParameter("status", status);
        return query.list();
    }

    public List<JobExecutionRequest> findAllJobExecutionRequests() {
        Session session = sessionFactory.getCurrentSession();
        Query<JobExecutionRequest> query = session.createQuery("from JobExecutionRequest", JobExecutionRequest.class);
        return query.list();
    }

}
