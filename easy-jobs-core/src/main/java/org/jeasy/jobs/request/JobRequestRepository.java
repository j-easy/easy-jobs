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
        sessionFactory.getCurrentSession().saveOrUpdate(jobRequest);
    }

    public void updateStatusAndProcessingDate(int jobRequestId, JobRequestStatus status, LocalDateTime processingDate) {
        Session session = sessionFactory.getCurrentSession();
        JobRequest jobRequest = session.get(JobRequest.class, jobRequestId);
        jobRequest.setStatus(status);
        jobRequest.setProcessingDate(processingDate);
        session.update(jobRequest);
    }

    public void updateStatus(int jobRequestId, JobRequestStatus status) {
        Session session = sessionFactory.getCurrentSession();
        JobRequest jobRequest = session.get(JobRequest.class, jobRequestId);
        jobRequest.setStatus(status);
        session.update(jobRequest);
    }

    public List<JobRequest> getPendingJobRequests() {
        // TODO order by priority
        Session session = sessionFactory.getCurrentSession();
        Query<JobRequest> query = session.createQuery("from JobRequest where status = :status ", JobRequest.class);
        query.setParameter("status", JobRequestStatus.PENDING);
        return query.list();
    }

    public List<JobRequest> findAllJobRequests() {
        Session session = sessionFactory.getCurrentSession();
        Query<JobRequest> query = session.createQuery("from JobRequest", JobRequest.class);
        return query.list();
    }

}
