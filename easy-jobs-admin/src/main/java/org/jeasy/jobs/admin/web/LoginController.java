package org.jeasy.jobs.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String login() {
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public String doLogin() {
        // TODO do login
        return "home";
    }

}
