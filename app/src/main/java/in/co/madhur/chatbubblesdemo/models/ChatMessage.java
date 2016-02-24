package in.co.madhur.chatbubblesdemo.models;

import java.util.Date;

import in.co.madhur.chatbubblesdemo.model.Status;
import in.co.madhur.chatbubblesdemo.model.UserType;

/**
 * Created by rube on 4/15/15.
 */
public class ChatMessage {

    private String from, message;
    private String to;
    private boolean isSelf;
    private Date messageTime;
    private boolean sent = false;
    private boolean delivered = false;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    private String messageID;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    private UserType userType;

    public Status getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    private Status messageStatus;

    public ChatMessage() {
    }

    public ChatMessage(String fromName, String message, Date messageTime, boolean isSelf, String to) {
        this.from = fromName;
        this.message = message;
        this.isSelf = isSelf;
        this.messageTime = messageTime;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
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
