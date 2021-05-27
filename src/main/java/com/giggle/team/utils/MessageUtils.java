package com.giggle.team.utils;

import com.giggle.team.models.Topic;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.TopicRepository;
import com.giggle.team.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class MessageUtils {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public MessageUtils(UserRepository userRepository, TopicRepository topicRepository) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    public boolean checkDestination(Principal principal, String destination) {
        UserEntity user = userRepository.findByEmail(principal.getName());
        String[] splitted = destination.split("/");
        Topic topic = topicRepository.findByStompDestination(splitted[splitted.length-1]);
        List<UserEntity> userEntities = topic.getUsers();
        if (user != null) {
            for (UserEntity userEntity:
                 userEntities) {
                if(userEntity.getEmail().equals(user.getEmail())){
                    return true;
                }
            }
        }
        return false;
    }

}
