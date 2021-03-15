package com.giggle.team.controller;

import com.giggle.team.models.ChatMessage;
import com.giggle.team.services.KafkaProducer;
import com.giggle.team.storage.MessageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/kafka/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private MessageStorage storage;

    /**
     * Receiving message from Web Browser using STOMP CLIENT and further Sending
     * message to a KAFKA TOPIC
     */
    @GetMapping(value = "/sendMessage")
    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) {
        logger.debug("ChatController.sendMessage : Received message from Web Browser using STOMP Client and further sending it to a KAFKA Topic");
        producer.send(message.getSender()
                + "-" + message.getContent()
                + "-" + message.getChatname()
                + "-" + ChatMessage.MessageType.valueOf(message.getType().name()));

    }

    /**
     * Adding username in Websocket
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        // fixme - why chatname hold in header
        headerAccessor.getSessionAttributes().put("chatname", chatMessage.getChatname());
        return chatMessage;
    }

    /**
     * It consumes messages from a specified Topic
     */
    @GetMapping(value = "/consumer")
    public String getAllRecievedMessage() {
        String messages = storage.toString();
        storage.clear();
        return messages;
    }

}
