package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JobsController extends AbstractController {

    @Autowired
    private JobRepository jobRepository;

    @RequestMapping("/jobs")
    public ModelAndView jobs() {
        ModelAndView modelAndView = new ModelAndView("jobs");
        modelAndView.addObject("jobs", jobRepository.findAll());
        return modelAndView;
    }

    @ModelAttribute("title")
    public String title() {
        return "Jobs";
    }

    @ModelAttribute("jobsPageActive")
    public boolean isActive() {
        return true;
    }

}
