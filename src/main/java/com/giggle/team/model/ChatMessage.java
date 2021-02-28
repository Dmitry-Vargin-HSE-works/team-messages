package com.giggle.team.model;

public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String chatId;

    @SuppressWarnings("unused")
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
    
    public ChatMessage() {
		super();
	}

	public ChatMessage(String chatId, MessageType message, String content, String sender) {
		super();
		this.chatId = chatId;
		this.type = message;
		this.content = content;
		this.sender = sender;
	}

	public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    @SuppressWarnings("unused")
    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatId() {
        return chatId;
    }

    @SuppressWarnings("unused")
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
	public String toString() {
		return "ChatMessage [type=" + type + ", content=" + content + ", sender=" + sender + "]";
	}
    
    
}