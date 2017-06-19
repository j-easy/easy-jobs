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

    public JobRequest getById(int id) {
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
        // TODO order by priority (keep something for v2)
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from JobRequest where status = :status ");
        query.setParameter("status", JobRequestStatus.PENDING);
        return query.list(); // todo argh untyped APIs.. use TypedQuery
    }

}
