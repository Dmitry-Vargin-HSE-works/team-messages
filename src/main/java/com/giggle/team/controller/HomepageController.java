package com.giggle.team.controller;

import com.giggle.team.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomepageController {

    @GetMapping(value = "/homepage")
    public ModelAndView homepage() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("homepage");
        return modelAndView;
    }

}
