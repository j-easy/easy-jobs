package org.jeasy.jobs.admin.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractController {

    @Value("${application.version}")
    private String applicationVersion;

    @ModelAttribute("version")
    public String version() {
        return applicationVersion;
    }

    @ModelAttribute("title")
    public abstract String title();

    public abstract boolean isActive();
}
