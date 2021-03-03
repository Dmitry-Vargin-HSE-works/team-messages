package com.giggle.team.controller;


import com.giggle.team.model.ChatMessage;
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
	KafkaProducer producer;

	@Autowired
	MessageStorage storage;
	
	/**
	 * Receiving message from Web Browser using STOMP CLIENT and further Sending
	 * message to a KAFKA TOPIC
	 * @return
	 */
	@GetMapping(value = "/sendMessage")
	@MessageMapping("/sendMessage")
	public void sendMessage(ChatMessage message) throws Exception {
		logger.debug("ChatController.sendMessage : Received message from Web Browser using STOMP Client and further sending it to a KAFKA Topic");

		System.out.print("\n\n");
		System.out.println("ChatController:sendMessage");
		System.out.println(message.getSender());
		System.out.println(message.getContent());
		System.out.println(message.getChatname());
		System.out.println(message.getType());
		System.out.print("\n\n");

		producer.send(message.getSender()
				+ "-" + message.getContent()
				+ "-" + message.getChatname()
				+ "-" + ChatMessage.MessageType.valueOf(message.getType().name()));
	}

	/**
	 * Adding username in Websocket
	 * @param chatMessage
	 * @param headerAccessor
	 * @return
	 */
	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session

		System.out.print("\n\n");
		System.out.println("ChatController:addUser");
		System.out.println(chatMessage.getSender());
		System.out.println(chatMessage.getContent());
		System.out.println(chatMessage.getChatname());
		System.out.println(chatMessage.getType());
		System.out.print("\n\n");

		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("chatname", chatMessage.getChatname());
		return chatMessage;
	}
	
	/**
	 * It consumes messages from a specified Topic
	 * @return
	 */
	@GetMapping(value = "/consumer")
	public String getAllRecievedMessage() {
		String messages = storage.toString();
		System.out.println("\n\nChatController:getAllRecievedMessage\n" + messages + "\n\n");
		storage.clear();
		return messages;
	}
}