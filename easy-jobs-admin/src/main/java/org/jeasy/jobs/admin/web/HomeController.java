package org.jeasy.jobs.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController extends AbstractController {

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

}
