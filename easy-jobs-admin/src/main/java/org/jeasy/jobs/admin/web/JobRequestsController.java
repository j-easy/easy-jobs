package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.request.JobRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JobRequestsController extends AbstractController {

    @Autowired
    private JobRequestRepository jobRequestRepository;

    @RequestMapping("/requests")
    public ModelAndView jobs() {
        ModelAndView modelAndView = new ModelAndView("requests");
        modelAndView.addObject("requests", jobRequestRepository.findAllJobRequests());
        return modelAndView;
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
