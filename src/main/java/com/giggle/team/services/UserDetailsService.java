package com.giggle.team.services;

import com.giggle.team.models.User;
import com.giggle.team.repositories.UserRepository;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService {

    private final BCryptPasswordEncoder encoder;

    private final AuthenticationManagerBuilder builder;

    private final UserRepository userRepository;

    public UserDetailsService(BCryptPasswordEncoder encoder,
                              AuthenticationManagerBuilder builder,
                              UserRepository userRepository) {
        this.encoder = encoder;
        this.builder = builder;
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean isPasswordCorrect(User user, String password) {
        return user.getPassword() != null && encoder.matches(password, user.getPassword());
    }

    public boolean saveUser(User user) {
        if (user == null || user.getEmail() == null ||
                user.getEmail().isEmpty() ||
                findUserByEmail(user.getEmail()) != null) return false;
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

}
