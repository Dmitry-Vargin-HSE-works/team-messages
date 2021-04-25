package com.giggle.team.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.giggle.team.config.View;
import com.giggle.team.models.User;
import com.giggle.team.services.UserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    public static final String phrase = "example";

    private final UserUtils userUtils;

    public UserController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> signup(@RequestBody User user) {
        return userUtils.saveUser(user) ? ResponseEntity.ok().build() : ResponseEntity.status(409).build();
    }

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<User> entity() {
        return ResponseEntity.ok(new User(phrase, phrase, phrase));
    }
}
