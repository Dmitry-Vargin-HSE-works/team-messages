package com.giggle.team.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.giggle.team.config.View;
import com.giggle.team.models.User;
import com.giggle.team.services.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    public static final String phrase = "example";

    private final UserDetailsService userDetailsService;

    public UserController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userDetailsService.saveUser(user)) return ResponseEntity.ok().build();
        else return ResponseEntity.status(409).build();
    }

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.GET, consumes = "application/json")
    public ResponseEntity<User> entity() {
        return ResponseEntity.ok(new User(phrase, phrase, phrase));
    }
}
