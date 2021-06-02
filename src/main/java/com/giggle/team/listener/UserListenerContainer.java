package com.giggle.team.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class UserListenerContainer {

    private ConcurrentMessageListenerContainer<String, String> container;
    private static final Logger logger = LoggerFactory.getLogger(UserListenerContainer.class);
    private final String user, chat;

    public UserListenerContainer(String kafkaTopic, String user, String chat,
                                 ConcurrentKafkaListenerContainerFactory<String, String> factory,
                                 SimpMessagingTemplate template, String id) {
        this.user = user;
        this.chat = chat;
        container = factory.createContainer(kafkaTopic);
        container.getContainerProperties().setGroupId(user);
        container.getContainerProperties().setAssignmentCommitOption(ContainerProperties.AssignmentCommitOption.NEVER);
        container.getContainerProperties().setMessageListener(new UserListener(template, chat, user, logger, id));
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

