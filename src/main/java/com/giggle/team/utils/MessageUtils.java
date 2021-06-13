package com.giggle.team.utils;

import com.giggle.team.models.Topic;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.TopicRepository;
import com.giggle.team.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;

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
        String[] split = destination.split("/");
        if(split[split.length - 1].equals("service")){
            return !Objects.isNull(user);
        }
        Topic topic = topicRepository.findByStompDestination(split[split.length - 1]);
        return !Objects.isNull(user) && !Objects.isNull(topic) && user.getTopics().contains(topic.getId());
    }


}
