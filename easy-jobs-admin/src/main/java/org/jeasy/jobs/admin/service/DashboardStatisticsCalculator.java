package org.jeasy.jobs.admin.service;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.execution.JobExecutionStatus;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.jeasy.jobs.request.JobExecutionRequestRepository;
import org.jeasy.jobs.request.JobExecutionRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class DashboardStatisticsCalculator {

    private JobRepository jobRepository;
    private JobExecutionRepository jobExecutionRepository;
    private JobExecutionRequestRepository jobExecutionRequestRepository;

    @Autowired
    public DashboardStatisticsCalculator(JobRepository jobRepository, JobExecutionRepository jobExecutionRepository, JobExecutionRequestRepository jobExecutionRequestRepository) {
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobExecutionRequestRepository = jobExecutionRequestRepository;
    }

    public DashboardStatistics calculateStatistics() {

        DashboardStatistics statistics = new DashboardStatistics();

        // calculate global stats
        statistics.setRegisteredJobs(jobRepository.findAll().size());

        List<JobExecution> jobExecutions = jobExecutionRepository.findAllJobExecutions();
        long runningExecutions = jobExecutions.stream().filter(running()).count();
        long failedExecutions = jobExecutions.stream().filter(failed()).count();
        int nbExecutions = jobExecutions.size();
        statistics.setRunningExecutions((int) runningExecutions);
        statistics.setFailedExecutions((int) failedExecutions);
        statistics.setTotalExecutions(nbExecutions);

        int pendingRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PENDING).size();
        statistics.setPendingRequests(pendingRequests);

        // calculate request number per hour stats
        final List<LocalDateTime> lastTwelveHours = getLastTwelveHours();

        List<JobExecutionRequest> jobExecutionRequests = jobExecutionRequestRepository.findAllJobExecutionRequests();
        List<Integer> jobExecutionRequestsForTheLastTwelveHours = new ArrayList<>();
        for (LocalDateTime hour : lastTwelveHours) {
            jobExecutionRequestsForTheLastTwelveHours.add((int) jobExecutionRequests.stream().filter(creationDateIsWithin(hour, hour.plusHours(1))).count());
        }
        statistics.setLastTwelveHours(lastTwelveHours);
        statistics.setReceivedRequestsForTheLastTwelveHours(jobExecutionRequestsForTheLastTwelveHours);

        // calculate job executions by status
        final LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        final LocalDateTime yesterday = today.minusDays(1).truncatedTo(ChronoUnit.DAYS);
        final LocalDateTime twoDaysAgo = today.minusDays(2).truncatedTo(ChronoUnit.DAYS);
        final LocalDateTime tomorrow = today.plusDays(1).truncatedTo(ChronoUnit.DAYS);

        List<Integer> succeededExecutionsList = new ArrayList<>();
        succeededExecutionsList.add((int) jobExecutions.stream().filter(succeeded()).filter(startDateIsWithin(twoDaysAgo, yesterday)).count());
        succeededExecutionsList.add((int) jobExecutions.stream().filter(succeeded()).filter(startDateIsWithin(yesterday, today)).count());
        succeededExecutionsList.add((int) jobExecutions.stream().filter(succeeded()).filter(startDateIsWithin(today, tomorrow)).count());

        List<Integer> failedExecutionsList = new ArrayList<>();
        failedExecutionsList.add((int) jobExecutions.stream().filter(failed()).filter(startDateIsWithin(twoDaysAgo, yesterday)).count());
        failedExecutionsList.add((int) jobExecutions.stream().filter(failed()).filter(startDateIsWithin(yesterday, today)).count());
        failedExecutionsList.add((int) jobExecutions.stream().filter(failed()).filter(startDateIsWithin(today, tomorrow)).count());

        statistics.setLastThreeDays(Arrays.asList(twoDaysAgo.toLocalDate(), yesterday.toLocalDate(), today.toLocalDate()));
        statistics.setSucceededExecutionsForTheLastThreeDays(succeededExecutionsList);
        statistics.setFailedExecutionsForTheLastThreeDays(failedExecutionsList);

        return statistics;
    }

    private Predicate<JobExecution> failed() {
        return jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.FAILED);
    }

    private Predicate<JobExecution> succeeded() {
        return jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.SUCCEEDED);
    }

    private Predicate<JobExecution> running() {
        return jobExecution -> jobExecution.getJobExecutionStatus().equals(JobExecutionStatus.RUNNING);
    }

    private Predicate<JobExecution> startDateIsWithin(LocalDateTime begin, LocalDateTime end) {
        return jobExecution -> {
            LocalDateTime startDate = jobExecution.getStartDate();
            return isWithin(startDate, begin, end);
        };
    }

    private Predicate<JobExecutionRequest> creationDateIsWithin(LocalDateTime begin, LocalDateTime end) {
        return jobExecutionRequest -> {
            LocalDateTime creationDate = jobExecutionRequest.getCreationDate();
            return isWithin(creationDate, begin, end);
        };
    }

    private boolean isWithin(LocalDateTime dateToCheck, LocalDateTime begin, LocalDateTime end) {
        return dateToCheck.isAfter(begin) && dateToCheck.isBefore(end);
    }

    private List<LocalDateTime> getLastTwelveHours() {
        LocalDateTime now = LocalDateTime.now();
        final List<LocalDateTime> lastTwelveHours = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            lastTwelveHours.add(now.minusHours(i).truncatedTo(ChronoUnit.HOURS));
        }
        return lastTwelveHours;
    }

}
