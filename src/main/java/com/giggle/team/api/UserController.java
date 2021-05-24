package com.giggle.team.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.UserRepository;
import com.giggle.team.utils.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    public static final String phrase = "example";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> signup(@RequestBody UserEntity userEntity) {
        userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        return ResponseEntity.ok().build();
    }

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<UserEntity> entity() {
        return ResponseEntity.ok(new UserEntity(phrase, phrase, phrase));
    }
}