package com.giggle.team.controllers;

import com.giggle.team.entities.ChatMessage;
import com.giggle.team.services.KafkaProducer;
import com.giggle.team.storage.MessageStorage;
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


    private final KafkaProducer producer;

    private final MessageStorage storage;

    @Autowired
    public ChatController(KafkaProducer producer, MessageStorage storage) {
        this.producer = producer;
        this.storage = storage;
    }

    /**
     * Receiving message from Web Browser using STOMP CLIENT and further Sending
     * message to a KAFKA TOPIC
     */
    @GetMapping(value = "/sendMessage")
    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) {
        producer.send(message.getContent() + "-"
                + message.getSender());
    }

    /**
     * Adding username in Websocket
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    /**
     * It consumes messages from a specified Topic
     */
    @GetMapping(value = "/consumer")
    public String getAllReceivedMessage() {
        String messages = storage.toString();
        storage.clear();
        return messages;
    }
}