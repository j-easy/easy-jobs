package org.jeasy.jobs.admin.web;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractController {

    @ModelAttribute("version")
    public String version() {
        // TODO get current version
        return "0.3-SNAPSHOT";
    }

    @ModelAttribute("title")
    public abstract String title();

    public abstract ModelAndView active(ModelAndView modelAndView);
}
