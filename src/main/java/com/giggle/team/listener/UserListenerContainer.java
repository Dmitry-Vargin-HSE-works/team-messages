package com.giggle.team.listener;

import com.giggle.team.models.ChatMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

public class UserListenerContainer {

    private ConcurrentMessageListenerContainer<String, String> container;
    private static final Logger logger = LoggerFactory.getLogger(UserListenerContainer.class);
    private final String user, chat;

    public UserListenerContainer(String kafkaTopic, String user, String chat,
                                 ConcurrentKafkaListenerContainerFactory<String, String> factory,
                                 SimpMessagingTemplate template) {
        this.user = user;
        this.chat = chat;
        container = factory.createContainer(kafkaTopic);
        container.getContainerProperties().setGroupId(user);
        container.getContainerProperties().setAssignmentCommitOption(ContainerProperties.AssignmentCommitOption.NEVER);
        container.getContainerProperties().setMessageListener(new UserListener(template, chat, user, logger));
        logger.info("Starting new listener for " + user + " in chat " + chat);
        container.start();
        logger.info("Started new listener for " + user + " in chat " + chat);
    }

    public UserListenerContainer(String user, String chat) {
        this.user = user;
        this.chat = chat;
    }

    public void stopContainer() {
        logger.info("Stopping listener for " + user + " in chat " + chat);
        container.stop();
        logger.info("Stopped listener for " + user + " in chat " + chat);
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (object instanceof UserListenerContainer) {
            isEqual = (this.user.equals(((UserListenerContainer) object).user)) && (this.chat.equals(((UserListenerContainer) object).chat));
        }
        return isEqual;
    }

}

/**
 * Implementing MessageListener to put it into Listener Container
 * Implementing ConsumerSeekAware to be able to set offset in kafka topic to 0 for each new listener
 **/
class UserListener implements ConsumerSeekAware, MessageListener<String, String> {
    SimpMessagingTemplate template;
    String chat, user;
    Logger logger;

    public UserListener(SimpMessagingTemplate template, String chat, String user, Logger logger) {
        this.template = template;
        this.chat = chat;
        this.user = user;
        this.logger = logger;
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
            logger.info("Got a new message for " + this.user + " ; From chat " + this.chat);
            template.convertAndSendToUser(this.user, "/queue/" + chat,
                    new ChatMessage(message[0], ChatMessage.MessageType.valueOf(message[1]), message[2], message[3]));
            //template.convertAndSend("/topic/user/" + user + "/" + chat, new ChatMessage(message[0], ChatMessage.MessageType.valueOf(message[1]), message[2], message[3]));
        }
    }
}
