package org.jeasy.jobs.admin.web;

import org.jeasy.jobs.Utils;
import org.jeasy.jobs.user.User;
import org.jeasy.jobs.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String login() {
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public ModelAndView doLogin(HttpServletRequest request, ModelAndView modelAndView, HttpSession session) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        User user = userRepository.getByNameAndPassword(name, Utils.md5(password));
        if(user == null) {
            modelAndView.setViewName("login");
            modelAndView.addObject("error", "Invalid username/password");
        } else {
            modelAndView.setViewName("redirect:/home");
            session.setAttribute("user", user);
        }
        return modelAndView;
    }

}
