package com.giggle.team.models;

public class Message {
    private MessageType type;
    private String content;
    private String sender;
    private String chatId;
    private String senderName;
    private String messageId;

    @SuppressWarnings("unused")
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        SYSTEM
    }

    public Message() {
        super();
    }

    public Message(String chatId, MessageType message, String content, String sender, String senderName, String messageId) {
        super();
        this.chatId = chatId;
        this.type = message;
        this.content = content;
        this.sender = sender;
        this.senderName = senderName;
        this.messageId = messageId;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "ChatMessage [type=" + type + ", content=" + content + ", sender=" + sender + "]";
    }

}