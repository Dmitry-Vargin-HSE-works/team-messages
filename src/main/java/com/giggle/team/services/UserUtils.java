package com.giggle.team.services;

import com.giggle.team.models.User;
import com.giggle.team.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserUtils {

    private final BCryptPasswordEncoder encoder;

    private final UserRepository userRepository;

    public UserUtils(BCryptPasswordEncoder encoder,
                     UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean isPasswordCorrect(User user, String password) {
        return user.getPassword() != null && encoder.matches(password, user.getPassword());
    }

    public boolean saveUser(User user) {
        if (user == null || user.getUsername() == null ||
                user.getUsername().isEmpty() ||
                findUserByUsername(user.getUsername()) != null) return false;
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }


}
