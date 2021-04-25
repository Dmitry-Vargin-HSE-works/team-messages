package com.giggle.team.controller;

import com.giggle.team.listener.UserListenerContainer;
import com.giggle.team.models.ChatMessage;
import com.giggle.team.services.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping(value = "/kafka/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
    private final SimpMessagingTemplate template;
    private final KafkaProducer producer;
    private final Map<String, ArrayList<UserListenerContainer>> listenersMap;

    @Value("${message-topic}")
    private String kafkaTopic;

    public ChatController(ConcurrentKafkaListenerContainerFactory<String, String> factory, SimpMessagingTemplate template, KafkaProducer producer, Map<String, ArrayList<UserListenerContainer>> listenersMap) {
        this.factory = factory;
        this.template = template;
        this.producer = producer;
        this.listenersMap = listenersMap;
    }

    /**
     * Receiving message from Web Browser using STOMP CLIENT and further Sending
     * message to a KAFKA TOPIC
     */
    @MessageMapping("/sendMessage")
    @RequestMapping(value = "/sendMessage", method = RequestMethod.GET, produces = "application/json")
    public void sendMessage(ChatMessage message) {
        logger.debug("ChatController.sendMessage : Received message from Web Browser using STOMP Client and further sending it to a KAFKA Topic");
        /*
         * Проверять с помощью авторизации доступен ли нужный чат
         * После этого уже отправлять сообщение в кафку
         *
         * fixme @Stanislav comment userRepository.find(message.getUser).map(...).orElseGet(() -> new ResponseEntity(HttpStatus.FORBIDDEN))
         */
        producer.send(message.getChatId() + "-" + ChatMessage.MessageType.valueOf(message.getType().name()) + "-" + message.getContent() + "-"
                + message.getSender());
    }

    /**
     * Adding username in Websocket
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.debug("added username in web socket session");
        // Add username in web socket session

        // fixme
        assert (headerAccessor != null);
        assert (headerAccessor.getSessionAttributes() != null);

        if (chatMessage == null || chatMessage.getSender() == null || chatMessage.getSender().isEmpty()) {
            headerAccessor.getSessionAttributes().put("username", "unknown");
        } else {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        }
        return chatMessage;
    }

    /**
     * Joining to a specific chat:
     * Creating kafka listener container for each user for each chat and putting it to map
     * Each listener will send received message to specific STOMP topic
     */
    @MessageMapping("/chat.join")
    public void joinChat(@Payload ChatMessage chatMessage) {
        logger.info("Received request for listener creation");
        if (!listenersMap.containsKey(chatMessage.getSender())) {
            logger.info("User's list of listeners not exist, creating new one");
            listenersMap.put(chatMessage.getSender(), new ArrayList<>());
            logger.info("Creating listener for " + chatMessage.getSender());
            listenersMap.get(chatMessage.getSender()).add(
                    new UserListenerContainer(kafkaTopic, chatMessage.getSender(), chatMessage.getChatId(), factory, template)
            );
        } else {
            if (!listenersMap.get(chatMessage.getSender()).contains(
                    new UserListenerContainer(chatMessage.getSender(), chatMessage.getChatId()))) {
                logger.info("No already existing listener, creating new one");
                listenersMap.get(chatMessage.getSender()).add(
                        new UserListenerContainer(kafkaTopic, chatMessage.getSender(), chatMessage.getChatId(), factory, template)
                );
            } else {
                logger.info("Such listener already exists");
            }
        }
    }

}


