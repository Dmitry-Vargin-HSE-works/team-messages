package com.giggle.team.listener;

import com.giggle.team.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class UserListener {

    private ConcurrentMessageListenerContainer<String, String> container;
    private static final Logger logger = LoggerFactory.getLogger(UserListener.class);
    private final String user, chat;

    public UserListener(String kafkaTopic, String user, String chat,
                        ConcurrentKafkaListenerContainerFactory<String, String> factory,
                        SimpMessagingTemplate template) {
        this.user = user;
        this.chat = chat;
        container = factory.createContainer(kafkaTopic);
        container.getContainerProperties().setGroupId(user);
        container.getContainerProperties().setAssignmentCommitOption(ContainerProperties.AssignmentCommitOption.NEVER);
        container.getContainerProperties().setMessageListener((MessageListener<String, String>) record -> {
            String[] message = record.value().split("-");
            if (message[0].equals(chat)) {
                logger.info("Got a new message for " + this.user + " ; From chat " + this.chat);
                template.convertAndSend("/topic/user/" + user + "/" + chat,
                        new ChatMessage(message[0], ChatMessage.MessageType.valueOf(message[1]), message[2], message[3]));
            }
        });
        logger.info("Starting new listener for " + user + " in chat " + chat);
        container.start();
        logger.info("Started new listener for " + user + " in chat " + chat);

    }

    public UserListener(String user, String chat) {
        this.user = user;
        this.chat = chat;
    }

    public ConcurrentMessageListenerContainer<String, String> getContainer() {
        return container;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (object instanceof UserListener) {
            isEqual = (this.user.equals(((UserListener) object).user)) && (this.chat.equals(((UserListener) object).chat));
        }
        return isEqual;
    }

}
