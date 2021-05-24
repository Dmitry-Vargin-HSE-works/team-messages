package com.giggle.team.listener;

import com.giggle.team.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, Map<String, ArrayList<UserListenerContainer>> listenersMap) {
        this.messagingTemplate = messagingTemplate;
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
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            for (int i = 0; i < listenersMap.get(username).size(); i++) {
                listenersMap.get(username).get(i).stopContainer();
            }
            listenersMap.remove(username);
            logger.info("User Disconnected : " + username, ", Listeners Removed");
            Message message = new Message();
            message.setType(Message.MessageType.LEAVE);
            message.setSender(username);
            messagingTemplate.convertAndSend("/topic/public", message);
        }
    }

}