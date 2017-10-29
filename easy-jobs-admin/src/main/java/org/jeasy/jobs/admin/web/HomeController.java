package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.execution.JobExecution;
import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.execution.JobExecutionStatus;
import org.jeasy.jobs.job.JobExitStatus;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.jeasy.jobs.request.JobExecutionRequestRepository;
import org.jeasy.jobs.request.JobExecutionRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController extends AbstractController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private JobExecutionRequestRepository jobExecutionRequestRepository;

    @RequestMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("home");

        // FIXME optimize queries for stats (should be done on the db level + move to a separate class)

        // calculate global stats
        int jobs = jobRepository.findAll().size();
        List<JobExecution> jobExecutions = jobExecutionRepository.findAllJobExecutions();
        int pendingRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PENDING).size();
        int submittedRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.SUBMITTED).size();
        int processedRequests = jobExecutionRequestRepository.findJobExecutionRequestsByStatus(JobExecutionRequestStatus.PROCESSED).size();
        long runningExecutions = jobExecutions.stream().filter(jobExecution -> jobExecution.getJobExecutionStatus().equals(JobExecutionStatus.RUNNING)).count();
        int nbExecutions = jobExecutions.size();
        long failedExecutions = jobExecutions.stream().filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.FAILED)).count();

        // calculate request number per hour stats
        LocalDateTime now = LocalDateTime.now();
        final List<LocalDateTime> lastTwelveHours = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            lastTwelveHours.add(now.minusHours(i).truncatedTo(ChronoUnit.HOURS));
        }

        List<JobExecutionRequest> jobExecutionRequests = jobExecutionRequestRepository.findAllJobExecutionRequests();
        List<Long> jobExecutionRequestsForTheLastTwelveHours = new ArrayList<>();
        for (LocalDateTime hour : lastTwelveHours) {
            jobExecutionRequestsForTheLastTwelveHours.add(jobExecutionRequests.stream()
                    .filter(jobExecutionRequest -> jobExecutionRequest.getCreationDate().isAfter(hour) &&
                            jobExecutionRequest.getCreationDate().isBefore(hour.plusHours(1)))
                    .count());
        }

        // calculate job executions by status
        final LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        final LocalDateTime yesterday = today.minusDays(1).truncatedTo(ChronoUnit.DAYS);
        final LocalDateTime twoDaysAgo = today.minusDays(2).truncatedTo(ChronoUnit.DAYS);
        final LocalDateTime tomorrow = today.plusDays(1).truncatedTo(ChronoUnit.DAYS);

        List<LocalDate> dates = new ArrayList<>();
        dates.add(twoDaysAgo.toLocalDate());
        dates.add(yesterday.toLocalDate());
        dates.add(today.toLocalDate());

        List<Long> finishedExecutionsList = new ArrayList<>();
        finishedExecutionsList.add(jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStartDate().isAfter(twoDaysAgo) && jobExecution.getStartDate().isBefore(yesterday))
                .filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.SUCCEEDED))
                .count());

        finishedExecutionsList.add(jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStartDate().isAfter(yesterday) && jobExecution.getStartDate().isBefore(today))
                .filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.SUCCEEDED))
                .count());

        finishedExecutionsList.add(jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStartDate().isAfter(today) && jobExecution.getStartDate().isBefore(tomorrow))
                .filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.SUCCEEDED))
                .count());

        List<Long> failedExecutionsList = new ArrayList<>();
        failedExecutionsList.add(jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStartDate().isAfter(twoDaysAgo) && jobExecution.getStartDate().isBefore(yesterday))
                .filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.FAILED))
                .count());

        failedExecutionsList.add(jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStartDate().isAfter(yesterday) && jobExecution.getStartDate().isBefore(today))
                .filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.FAILED))
                .count());

        failedExecutionsList.add(jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStartDate().isAfter(today) && jobExecution.getStartDate().isBefore(tomorrow))
                .filter(jobExecution -> jobExecution.getJobExitStatus().equals(JobExitStatus.FAILED))
                .count());

        // update model
        modelAndView.addObject("nbJobs", jobs);
        modelAndView.addObject("nbRunningExecutions", runningExecutions);
        modelAndView.addObject("pcFailedExecutions", ((int)(failedExecutions * 100.0 / nbExecutions + 0.5)));
        modelAndView.addObject("nbPendingRequests", pendingRequests);
        modelAndView.addObject("nbSubmittedRequests", submittedRequests);
        modelAndView.addObject("nbProcessedRequests", processedRequests);
        modelAndView.addObject("lastTwelveHours", getHours(lastTwelveHours));
        modelAndView.addObject("nbRequestsForLastTwelveHours", getNbRequestsHours(jobExecutionRequestsForTheLastTwelveHours));
        modelAndView.addObject("lastThreeDays", getDates(dates));
        modelAndView.addObject("failedExecutions", getFailedExecutions(failedExecutionsList));
        modelAndView.addObject("finishedExecutions", getFinishedExecutions(finishedExecutionsList));

        return modelAndView;
    }

    private String getFinishedExecutions(List<Long> finishedExecutionsList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Long finishedExecutionCount : finishedExecutionsList) {
            stringBuilder.append(finishedExecutionCount).append(" ");
        }
        return stringBuilder.toString();
    }

    private String getFailedExecutions(List<Long> failedExecutionsList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Long failedExecutionCount : failedExecutionsList) {
            stringBuilder.append(failedExecutionCount).append(" ");
        }
        return stringBuilder.toString();
    }

    private String getDates(List<LocalDate> dates) {
        StringBuilder stringBuilder = new StringBuilder();
        for (LocalDate date : dates) {
            stringBuilder.append(date).append(" ");
        }
        return stringBuilder.toString();
    }

    private String getNbRequestsHours(List<Long> jobExecutionRequestsForTheLastTwelveHours) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Long nbRequests : jobExecutionRequestsForTheLastTwelveHours) {
            stringBuilder.append(nbRequests).append(" ");
        }
        return stringBuilder.toString();
    }

    private String getHours(List<LocalDateTime> lastTwelveHours) {
        StringBuilder stringBuilder = new StringBuilder();
        for (LocalDateTime hour : lastTwelveHours) {
            stringBuilder.append(hour.getHour()).append("h ");
        }
        return stringBuilder.toString();
    }

    @ModelAttribute("title")
    public String title() {
        return "Dashboard (Last update: " + LocalDateTime.now() + ")";
    }

    @ModelAttribute("homePageActive")
    public boolean isActive() {
        return true;
    }

}
