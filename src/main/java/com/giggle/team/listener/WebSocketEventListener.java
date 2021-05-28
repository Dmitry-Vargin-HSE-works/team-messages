package com.giggle.team.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Map;

@Component
public class WebSocketEventListener {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
  private final Map<String, ArrayList<UserListenerContainer>> listenersMap;

  public WebSocketEventListener(Map<String, ArrayList<UserListenerContainer>> listenersMap) {
    this.listenersMap = listenersMap;
  }

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    logger.info(
            "WebSocketEventListener.handleWebSocketConnectListener:: NEW USER ADDED : Received a new web socket connection");
  }

  /**
   * Removing all of the user`s listeners on it disconnection from websocket
   */
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    logger.info("WebSocketEventListener.handleWebSocketDisconnectListener");
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    assert (headerAccessor.getSessionAttributes() != null);
    if(listenersMap.get(event.getSessionId()) != null){
      for (int i = 0; i < listenersMap.get(event.getSessionId()).size(); i++) {
        listenersMap.get(event.getSessionId()).get(i).stopContainer();
        logger.info("Listener removed");
      }
      listenersMap.remove(event.getSessionId());
      logger.info("User Disconnected : " + event.getSessionId(), ", Listeners Removed");
    }
  }

}