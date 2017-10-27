package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.job.JobRepository;
import org.jeasy.jobs.request.JobExecutionRequest;
import org.jeasy.jobs.request.JobExecutionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static java.lang.Integer.parseInt;

@Controller
public class JobExecutionRequestsController extends AbstractController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobExecutionRequestRepository jobExecutionRequestRepository;

    @RequestMapping("/requests")
    public ModelAndView requests() {
        ModelAndView modelAndView = new ModelAndView("requests");
        modelAndView.addObject("requests", jobExecutionRequestRepository.findAllJobExecutionRequests());
        return modelAndView;
    }

    @RequestMapping(path = "/requests/new", method = RequestMethod.GET)
    public ModelAndView newJobExecutionRequestForm() {
        ModelAndView modelAndView = new ModelAndView("new-request");
        modelAndView.addObject("jobs", jobRepository.findAll());
        return modelAndView;
    }

    @RequestMapping(path = "/requests/new", method = RequestMethod.POST)
    public ModelAndView createNewJobExecutionRequest(HttpServletRequest request) {
        String jobId = request.getParameter("jobId");
        String jobParameters = request.getParameter("jobParameters");
        jobExecutionRequestRepository.save(new JobExecutionRequest(parseInt(jobId), jobParameters));
        return requests();
    }

    @ModelAttribute("title")
    public String title() {
        return "Job requests";
    }

    @ModelAttribute("requestsPageActive")
    public boolean isActive() {
        return true;
    }

}
