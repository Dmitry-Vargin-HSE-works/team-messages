package com.giggle.team.controller;

import com.giggle.team.models.Topic;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.TopicRepository;
import com.giggle.team.repositories.UserRepository;
import com.giggle.team.utils.MessageUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@RestController
public class ChatSelectorController {
  private final UserRepository userRepository;
  private final TopicRepository topicRepository;
  private final MessageUtils messageUtils;

  public ChatSelectorController(UserRepository userRepository, TopicRepository topicRepository, MessageUtils messageUtils) {
    this.userRepository = userRepository;
    this.topicRepository = topicRepository;
    this.messageUtils = messageUtils;
  }

  @RequestMapping(value = "/find/user", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public List<String> findUsers(@RequestParam("query") String query){
    List<UserEntity> queryResult = userRepository.findByUsernameStartsWith(query);
    List<String> toSend = new LinkedList<>();
    for (UserEntity entity:
         queryResult) {
      toSend.add(entity.getUsername());
    }
    return toSend;
  }

  @RequestMapping(value = "/show/topic", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public List<String> showTopics(Principal principal){
    List<Topic> queryResult = topicRepository.findAll();
    List<String> toSend = new LinkedList<>();
    for (Topic topic:
            queryResult) {
      if(messageUtils.checkDestination(principal, topic.getStompDestination())){
        toSend.add(topic.getKafkaTopic());
      }
    }
    return toSend;
  }
}
