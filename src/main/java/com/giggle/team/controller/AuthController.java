package com.giggle.team.controller;

import com.giggle.team.models.User;
import com.giggle.team.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder builder;
    private final UserRepository userRepository;

    public AuthController(PasswordEncoder bCryptPasswordEncoder,
                          AuthenticationManagerBuilder authenticationManagerBuilder,
                          UserRepository userRepository) {
        this.encoder = bCryptPasswordEncoder;
        this.builder = authenticationManagerBuilder;
        this.userRepository = userRepository;
    }


    @RequestMapping(value = "", method = RequestMethod.OPTIONS, produces = "application/json")
    public ResponseEntity<?> collectionOptions() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
                .build();
    }

    /**
     * in memory temp authentication and user save to database
     *
     * @param user - get the description from GET request
     * @return Http status 200 or 409
     */
    @SneakyThrows
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> postUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("User already exist", HttpStatus.CONFLICT);
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            builder.inMemoryAuthentication()
                    .withUser(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER", "ADMIN")
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * an example of user for post request
     *
     * @return User json representation
     */
    @RequestMapping(value = "/example", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<User> getUserExample() {
        return new ResponseEntity<>(new User("admin",
                "pupuk",
                true,
                true,
                true,
                true,
                Collections.singleton(new SimpleGrantedAuthority("USER"))), HttpStatus.OK);
    }

    /**
     * find user by username
     *
     * @return user representation or 404 status
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

