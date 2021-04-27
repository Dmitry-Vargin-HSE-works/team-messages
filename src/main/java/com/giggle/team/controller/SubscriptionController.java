package com.giggle.team.controller;


import com.giggle.team.listener.UserListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.security.Principal;
import java.util.Objects;

public class SubscriptionController implements ChannelInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
    if(Objects.equals(headerAccessor.getCommand(), StompCommand.SUBSCRIBE)) {
      Principal principal = headerAccessor.getUser();
      logger.info("Got new request for subscription to " + headerAccessor.getDestination());
      if (principal != null) {
        logger.info("Got new request for subscription to " + headerAccessor.getDestination() + " from " + principal.getName());
        if (!checkStompDestination(principal, headerAccessor.getDestination())) {
          throw new MessagingException("Requested destination is not available for this user");
        }
      } else {
        logger.info("Got new request for subscription to " + headerAccessor.getDestination() + " from not authenticated user");
        throw new MessagingException("Not authenticated");
      }
    }
    return message;
  }

  private boolean checkStompDestination(Principal principal, String stompDestination){
    //TODO insert checking if requested destination is available for provided principal
    return true;
  }
}
