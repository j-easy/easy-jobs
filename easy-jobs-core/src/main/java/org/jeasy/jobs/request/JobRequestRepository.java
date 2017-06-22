package org.jeasy.jobs.request;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class JobRequestRepository {

    private SessionFactory sessionFactory;

    public JobRequestRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public JobRequest findById(int id) {
        return sessionFactory.getCurrentSession().get(JobRequest.class, id);
    }

    public void save(JobRequest jobRequest) {
        sessionFactory.getCurrentSession().save(jobRequest);
    }

    public void update(JobRequest jobRequest) {
        sessionFactory.getCurrentSession().update(jobRequest);
    }

    public List<JobRequest> findJobRequestsByStatus(JobRequestStatus status) {
        // TODO order by priority
        Session session = sessionFactory.getCurrentSession();
        Query<JobRequest> query = session.createQuery("from JobRequest where status = :status ", JobRequest.class);
        query.setParameter("status", status);
        return query.list();
    }

    public List<JobRequest> findAllJobRequests() {
        Session session = sessionFactory.getCurrentSession();
        Query<JobRequest> query = session.createQuery("from JobRequest", JobRequest.class);
        return query.list();
    }

}
