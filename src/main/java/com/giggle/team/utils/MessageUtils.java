package com.giggle.team.utils;

import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class MessageUtils {

    private final UserRepository userRepository;

    public MessageUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkDestination(Principal principal, String destination) {
        /*UserEntity user = userRepository.findByUsername(principal.getName());
        if (user != null && user.getTopics() != null) {
            String[] splitDest = destination.split("/");
            return user.getTopics().stream().anyMatch(topic -> topic.getStompDestination().equals(splitDest[splitDest.length - 1]));
        }
        return false;*/
        return true;
    }

}
