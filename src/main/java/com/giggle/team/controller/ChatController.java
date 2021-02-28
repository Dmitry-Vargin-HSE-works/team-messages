package com.giggle.team.controller;

import com.giggle.team.model.ChatMessage;
import com.giggle.team.services.KafkaProducer;
import com.giggle.team.storage.MessageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/kafka/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
    private final SimpMessagingTemplate template;
    private final KafkaProducer producer;
    private final MessageStorage storage;

    public ChatController(ConcurrentKafkaListenerContainerFactory<String, String> factory, SimpMessagingTemplate template, KafkaProducer producer, MessageStorage storage) {
        this.factory = factory;
        this.template = template;
        this.producer = producer;
        this.storage = storage;
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
     * It consumes messages from a specified Topic
     */
    @RequestMapping(value = "/consumer", method = RequestMethod.GET, produces = "application/json")
    public String getAllReceivedMessage() {
        String messages = storage.toString();
        storage.clear();
        return messages;
    }

    @MessageMapping("/chat.getHistory")
    public void getHistory(@Payload ChatMessage chatMessage) {
        ConcurrentMessageListenerContainer<String, String> container = factory.createContainer("Chat-Topic");
        container.getContainerProperties().setGroupId("group");
        container.getContainerProperties().setAssignmentCommitOption(ContainerProperties.AssignmentCommitOption.NEVER);
        container.getContainerProperties().setMessageListener((MessageListener<String, String>) record -> {
            String[] message = record.value().split("-");
            System.out.println("/history/" + chatMessage.getChatId() + "-" + chatMessage.getSender());
            System.out.println(template.getUserDestinationPrefix());
            template.convertAndSend("/topic/history/" + chatMessage.getChatId() + "-" + chatMessage.getSender(),
                    new ChatMessage(message[0], ChatMessage.MessageType.valueOf(message[1]), message[2], message[3]));
        });
        container.start();
        System.out.println("STARTED");
    }
}


