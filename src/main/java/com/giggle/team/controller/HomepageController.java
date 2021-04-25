package com.giggle.team.controller;

import com.giggle.team.models.User;
import com.giggle.team.services.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomepageController {

    private final UserDetailsService userDetailsService;

    public HomepageController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/loginform.html", method = RequestMethod.GET)
    public String loginform() {
        return "loginform.html";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute(name = "email") String email,
                        @ModelAttribute(name = "password") String password) {
        User user = userDetailsService.findUserByEmail(email);
            return user == null ? "/loginform.html" :
                userDetailsService.isPasswordCorrect(user, password) ? "/chatform.html" : "/loginform.html";
    }

    @RequestMapping(value = "/chatform.html", method = RequestMethod.GET)
    public String chatform() {
        return "/chatform.html";
    }

}
