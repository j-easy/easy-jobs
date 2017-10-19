package org.jeasy.jobs.admin.web;

import org.springframework.web.bind.annotation.ModelAttribute;

public class AbstractController {

    @ModelAttribute("version")
    public String version() {
        // TODO get current version
        return "0.3-SNAPSHOT";
    }
}
