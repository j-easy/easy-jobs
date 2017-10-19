package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.execution.JobExecutionRepository;
import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController extends AbstractController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private JobRequestRepository jobRequestRepository;

    @RequestMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("nbJobs", jobRepository.findAll().size());
        modelAndView.addObject("nbExecutions", jobExecutionRepository.findAllJobExecutions().size());
        modelAndView.addObject("nbRequests", jobRequestRepository.findAllJobRequests().size());
        return modelAndView;
    }

    @ModelAttribute("title")
    public String title() {
        return "Dashboard";
    }

    @ModelAttribute("homePageActive")
    public boolean isActive() {
        return true;
    }

}
