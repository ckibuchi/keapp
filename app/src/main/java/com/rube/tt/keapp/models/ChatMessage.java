package com.rube.tt.keapp.models;

/**
 * Created by rube on 4/15/15.
 */
public class ChatMessage {

    private String fromName, message;
    private String to;
    private boolean isSelf;
    private String chatDate;
    private boolean sent = false;
    private boolean delivered = false;

    public ChatMessage() {
    }

    public ChatMessage(String fromName, String message, String chatDate, boolean isSelf, String to) {
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
        this.chatDate = chatDate;
        this.to = to;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
            this.isSelf = isSelf;
        }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
