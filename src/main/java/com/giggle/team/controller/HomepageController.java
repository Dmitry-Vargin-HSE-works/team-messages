package com.giggle.team.controller;

import com.giggle.team.models.User;
import com.giggle.team.services.UserService;
import com.giggle.team.services.UserUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomepageController {

    private final UserUtils userUtils;

    private final UserService userService;

    public HomepageController(UserUtils userUtils, UserService userService) {
        this.userUtils = userUtils;
        this.userService = userService;
    }

    @RequestMapping(value = "/loginform.html", method = RequestMethod.GET)
    public String loginform() {
        return "loginform.html";
    }

    @RequestMapping(value = "/loginform.html", method = RequestMethod.POST)
    public String authentication(@ModelAttribute(name = "username") String username,
                                 @ModelAttribute(name = "password") String password) {
        User user = userUtils.findUserByUsername(username);
        UserDetails principal = userService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return user == null ? "/loginform.html" :
                userUtils.isPasswordCorrect(user, password) ? "/chatform.html" : "/loginform.html";
    }

    @RequestMapping(value = "/chatform.html", method = RequestMethod.GET)
    public String chatform() {
        return "/chatform.html";
    }

}
