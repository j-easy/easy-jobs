package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.execution.JobExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JobExecutionsController extends AbstractController {

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @RequestMapping("/executions")
    public ModelAndView jobs() {
        ModelAndView modelAndView = new ModelAndView("executions");
        modelAndView.addObject("executions", jobExecutionRepository.findAllJobExecutions());
        return modelAndView;
    }

    @ModelAttribute("title")
    public String title() {
        return "Job executions";
    }

    @ModelAttribute("executionsPageActive")
    public boolean isActive() {
        return true;
    }

}
