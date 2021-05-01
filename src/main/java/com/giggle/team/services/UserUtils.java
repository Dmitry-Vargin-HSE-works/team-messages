package com.giggle.team.services;

import com.giggle.team.models.Topic;
import com.giggle.team.models.User;
import com.giggle.team.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

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

    public boolean checkDestination(Principal principal, String destination) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        String[] splitDest = destination.split("/");
        if (user != null && user.getTopics() != null) {
            for (Topic topic :
                    user.getTopics()) {
                if (topic.getStompDestination().equals(splitDest[splitDest.length - 1])) {
                    return true;
                }
            }
        }
        return false;
    }

}
