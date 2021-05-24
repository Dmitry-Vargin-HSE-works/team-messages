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
