package com.giggle.team.controller;

import com.giggle.team.models.User;
import com.giggle.team.repositories.UserRepository;
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

@RequestMapping(value = "/auth")
@RestController
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

    /**
     * in memory authentication
     *
     * @param user - get the description from GET request
     * @return Http status 200 or 500
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> userPost(@RequestBody User user) {
        try {
            builder.inMemoryAuthentication()
                    .withUser(user.getUsername())
                    .password(encoder.encode(user.getPassword()))
                    .roles("USER", "ADMIN")
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * database authentication
     *
     * @param user - get the description from GET request
     * @return Http status 200 or 500
     */
    @RequestMapping(value = "database", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> userPostDatabase(@RequestBody User user) {
        try {
            userRepository.save(user);
            builder.inMemoryAuthentication()
                    .withUser(user.getUsername())
                    .password(encoder.encode(user.getPassword()))
                    .roles("USER", "ADMIN")
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * an example of user for post request
     *
     * @return User json representation
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<User> userGet() {
        return new ResponseEntity<>(new User("example",
                "exampe",
                true,
                true,
                true,
                true,
                Collections.singleton(new SimpleGrantedAuthority("USER"))), HttpStatus.OK);
    }
}

