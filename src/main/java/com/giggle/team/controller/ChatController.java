package com.giggle.team.controller;

import com.giggle.team.listener.UserListenerContainer;
import com.giggle.team.models.Message;
import com.giggle.team.models.Topic;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.TopicRepository;
import com.giggle.team.repositories.UserRepository;
import com.giggle.team.services.KafkaProducer;
import com.giggle.team.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/kafka/chat")
public class ChatController {

  private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

  private final Map<String, ArrayList<UserListenerContainer>> listenersMap;

  private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
  private final MessageUtils messageUtils;
  private final SimpMessagingTemplate template;
  private final KafkaProducer producer;
  private final TopicRepository topicRepository;
  private final UserRepository userRepository;
  private final KafkaProducer kafkaProducer;
  @Value("${message-topic}")
  private String kafkaTopic;

  public ChatController(ConcurrentKafkaListenerContainerFactory<String, String> factory,
                        Map<String, ArrayList<UserListenerContainer>> listenersMap,
                        SimpMessagingTemplate template,
                        KafkaProducer producer,
                        MessageUtils messageUtils, TopicRepository topicRepository, UserRepository userRepository, KafkaProducer kafkaProducer) {
    this.factory = factory;
    this.listenersMap = listenersMap;
    this.template = template;
    this.producer = producer;
    this.messageUtils = messageUtils;
    this.topicRepository = topicRepository;
    this.userRepository = userRepository;
    this.kafkaProducer = kafkaProducer;
  }

  /**
   * Receiving message from Web Browser using STOMP CLIENT and further Sending
   * message to a KAFKA TOPIC
   */
  @MessageMapping("/sendMessage")
  @RequestMapping(value = "/sendMessage", method = RequestMethod.GET, produces = "application/json")
  public void sendMessage(Principal principal, @Payload Message message) {
    if (principal != null && messageUtils.checkDestination(principal, message.getChatId())) {
      logger.info("Got new message from " + principal.getName() + " to " + message.getChatId());
      producer.send(message.getChatId(), message.getChatId() + "-" + Message.MessageType.valueOf(message.getType().name())
              + "-" + message.getContent() + "-" + message.getSender());
      logger.info("Message to " + message.getChatId() + " from " + principal.getName() + " was sent");
    }else {
      logger.info("Message to " + message.getChatId() + " was not sent");
    }
  }

  /**
   * Adding username in Websocket
   */
  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
    logger.debug("added username in web socket session");
    // Add username in web socket session

    // fixme
    assert (headerAccessor != null);
    assert (headerAccessor.getSessionAttributes() != null);

    if (message == null || message.getSender() == null || message.getSender().isEmpty()) {
      headerAccessor.getSessionAttributes().put("username", "unknown");
    } else {
      headerAccessor.getSessionAttributes().put("username", message.getSender());
    }
    return message;
  }

  /**
   * Joining to a specific chat:
   * Creating kafka listener container for each user for each chat and putting it to map
   * Each listener will send received message to specific STOMP topic
   */
  @MessageMapping("/chat.join")
  public void joinChat(Principal principal, @Payload Message message, @Header("simpSessionId") String sessionId) {
    if (messageUtils.checkDestination(principal, message.getChatId())) {
      logger.info("Received request for listener creation from " + sessionId);
      if (!listenersMap.containsKey(sessionId)) {
        logger.info("User's list of listeners not exist, creating new one");
        listenersMap.put(sessionId, new ArrayList<>());
        logger.info("Creating listener for " + sessionId);
        listenersMap.get(sessionId).add(
                new UserListenerContainer(kafkaTopic, principal.getName(), message.getChatId(), factory, template)
        );
      } else {
        if (!listenersMap.get(sessionId).contains(
                new UserListenerContainer(principal.getName(), message.getChatId()))) {
          logger.info("No already existing listener, creating new one");
          listenersMap.get(sessionId).add(
                  new UserListenerContainer(kafkaTopic, principal.getName(), message.getChatId(), factory, template)
          );
        } else {
          logger.info("Such listener already exists");
        }
      }
    } else {
      logger.info("Received request for listener creation but access denied or principal is null");
    }
  }

  @RequestMapping(value = "/createChat", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public void createChat(Principal principal, @RequestParam("chatWith") String secondUsername){
    UserEntity user1 = userRepository.findByEmail(principal.getName());
    UserEntity user2 = userRepository.findByUsername(secondUsername);
    String chatName = (user1.getUsername() + "_" + user2.getUsername()).replaceAll("\\s+","");
    boolean topicExists = false;
    List<Topic> topics= topicRepository.findAll();
    for (Topic topic:
         topics) {
      if(!topic.getStompDestination().equals("main")){
        if((topic.getUsers().get(0).getEmail().equals(user1.getEmail()) && topic.getUsers().get(1).getEmail().equals(user2.getEmail())) ||
                (topic.getUsers().get(0).getEmail().equals(user2.getEmail()) && topic.getUsers().get(1).getEmail().equals(user1.getEmail()))){
          topicExists = true;
          break;
        }
      }
    }
    if(!topicExists){
      Topic toCreate = new Topic(chatName, chatName);
      toCreate.addUser(user1);
      toCreate.addUser(user2);
      topicRepository.save(toCreate);
      kafkaProducer.send(chatName, chatName + "-" + "SYSTEM"
              + "-" + "NEW CHAT CREATED" + "-" + user1.getUsername());
    }
  }
}



