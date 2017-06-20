package org.jeasy.jobs.execution;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.jeasy.jobs.job.JobExitStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class JobExecutionRepository {

    private SessionFactory sessionFactory;

    public JobExecutionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public JobExecution getByJobRequestId(int jobRequestId) {
        Session session = sessionFactory.getCurrentSession();
        Query<JobExecution> query = session.createQuery("from JobExecution where requestId = :requestId ", JobExecution.class);
        query.setParameter("requestId", jobRequestId);
        return query.getSingleResult();
    }

    public List<JobExecution> findAllJobExecutions() {
        Session session = sessionFactory.getCurrentSession();
        Query<JobExecution> query = session.createQuery("from JobExecution", JobExecution.class);
        return query.list();
    }

    public void save(JobExecution jobExecution) {
        sessionFactory.getCurrentSession().saveOrUpdate(jobExecution);
    }

    public void update(int jobRequestId, JobExitStatus jobExitStatus, LocalDateTime endDate) {
        Session session = sessionFactory.getCurrentSession();
        JobExecution jobExecution = getByJobRequestId(jobRequestId);
        jobExecution.setJobExecutionStatus(JobExecutionStatus.FINISHED);
        jobExecution.setJobExitStatus(jobExitStatus);
        jobExecution.setEndDate(endDate);
        session.update(jobExecution);
    }

}
