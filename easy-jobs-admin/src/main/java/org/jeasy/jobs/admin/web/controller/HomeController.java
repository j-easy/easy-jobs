package org.jeasy.jobs.admin.web.controller;

import org.jeasy.jobs.admin.service.DashboardStatistics;
import org.jeasy.jobs.admin.service.DashboardStatisticsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
public class HomeController extends AbstractController {

    @Autowired
    private DashboardStatisticsCalculator statisticsCalculator;

    @RequestMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("home");
        DashboardStatistics dashboardStatistics = statisticsCalculator.calculateStatistics();

        // global stats
        modelAndView.addObject("nbJobs", dashboardStatistics.getRegisteredJobs());
        modelAndView.addObject("nbRunningExecutions", dashboardStatistics.getRunningExecutions());
        modelAndView.addObject("pcFailedExecutions", dashboardStatistics.getPercentageOfFailedExecutions());
        modelAndView.addObject("nbPendingRequests", dashboardStatistics.getPendingRequests());

        // executions chart (on the left)
        modelAndView.addObject("lastTwelveHours", dashboardStatistics.getFormattedLastTwelveHours());
        modelAndView.addObject("nbRequestsForLastTwelveHours", dashboardStatistics.getFormattedReceivedRequestsForTheLastTwelveHours());

        // requests chart (on the right)
        modelAndView.addObject("lastThreeDays", dashboardStatistics.getFormattedLastThreeDays());
        modelAndView.addObject("failedExecutions", dashboardStatistics.getFormattedFailedExecutionsForTheLastThreeDays());
        modelAndView.addObject("succeededExecutions", dashboardStatistics.getFormattedSucceededExecutionsForTheLastThreeDays());

        return modelAndView;
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
