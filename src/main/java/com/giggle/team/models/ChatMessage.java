package com.giggle.team.models;

public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String chatname;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
    
    public ChatMessage() {
		super();
	}

	public ChatMessage(String sender, String content, String chatname, MessageType message) {
		super();
        this.sender = sender;
		this.content = content;
		this.chatname = chatname;
        this.type = message;
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

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatname() {
        return this.chatname;
    }

    public void setChatname(String chatname) {
        this.chatname = chatname;
    }

	@Override
	public String toString() {
        return "ChatMessage [sender=" + sender +
                ", content=" + content + "," +
                " chatname=" + chatname +
                ",type = " + type;
		//return "ChatMessage [type=" + type + ", content=" + content + ", sender=" + sender + "]";
	}
    
    
}