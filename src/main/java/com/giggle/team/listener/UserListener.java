package com.giggle.team.listener;

import com.giggle.team.models.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

/**
 * Implementing MessageListener to put it into Listener Container
 * Implementing ConsumerSeekAware to be able to set offset in kafka topic to 0 for each new listener
 **/
public class UserListener implements ConsumerSeekAware, MessageListener<String, String> {

    SimpMessagingTemplate template;
    String chat, user, id;
    Logger logger;

    public UserListener(SimpMessagingTemplate template, String chat, String user, Logger logger, String id) {
        this.template = template;
        this.chat = chat;
        this.user = user;
        this.logger = logger;
        this.id = id;
    }

    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        callback.seekToBeginning(assignments.keySet());
    }

    /**
     * Receiving message from kafka topic and checking if that message is belongs to the listeners chat
     * Then sending it to the STOMP topic that user subscribed
     */
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String[] message = data.value().split("-");
        if (message[0].equals(chat)) {
            logger.info("Got a new message for " + this.user + " ; From chat " + this.chat + " with listener " + id);
            template.convertAndSendToUser(user, "/queue/" + chat,
                    new Message(message[0], Message.MessageType.valueOf(message[1]), message[2], message[3], message[4], message[5]));
            //template.convertAndSend("/topic/user/" + user + "/" + chat, new ChatMessage(message[0], ChatMessage.MessageType.valueOf(message[1]), message[2], message[3]));
        }
    }

}
