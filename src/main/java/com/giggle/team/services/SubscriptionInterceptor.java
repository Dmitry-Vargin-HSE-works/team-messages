package com.giggle.team.services;



import com.giggle.team.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;

@Service
public class  SubscriptionInterceptor implements ChannelInterceptor {

  private final MessageUtils messageUtils;

  public SubscriptionInterceptor(MessageUtils messageUtils) {
    this.messageUtils = messageUtils;
  }

  private static final Logger logger = LoggerFactory.getLogger(SubscriptionInterceptor.class);

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
    if (Objects.equals(headerAccessor.getCommand(), StompCommand.SUBSCRIBE)) {
      Principal principal = headerAccessor.getUser();
      if (principal != null) {
        logger.info("Got new request for subscription to " + headerAccessor.getDestination() + " from " + principal.getName());
        if (!messageUtils.checkDestination(principal, headerAccessor.getDestination())) {
          logger.info("Requested destination is not available for this user");
          message = null;
        } else {
          logger.info("Access to " + headerAccessor.getDestination() + " granted");
        }
      } else {
        logger.info("Got new request for subscription to " + headerAccessor.getDestination() + " from not authenticated user");
        message = null;
      }
    }
    return message;
  }
}
