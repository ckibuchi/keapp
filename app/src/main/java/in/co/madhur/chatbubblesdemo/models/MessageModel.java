package in.co.madhur.chatbubblesdemo.models;

import in.co.madhur.chatbubblesdemo.model.Status;

/**
 * Created by rube on 4/8/15.
 */
public class MessageModel {


    private String name;
    private int mediaId = 0;
    private String date;
    private String message;
    private int profilePhotoId = 0;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    private String to;

    public Status getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    private Status messageStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){

        return  this.getName()+ " - " +this.getMessage();

    }

    public int getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(int profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }
}
