package com.giggle.team.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomepageController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String loginform() {
        return "loginform.html";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String login(@ModelAttribute(name = "email") String email,
                        @ModelAttribute(name = "password") String password,
                        Model model) {
        if ("spring".equals(email) && "root".equals(password))
            return "im";
        return "";
    }

    @RequestMapping(value = "im", method = RequestMethod.GET)
    public String chatform() {
        return "chatform.html";
    }

}
