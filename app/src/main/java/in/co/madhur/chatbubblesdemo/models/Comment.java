package in.co.madhur.chatbubblesdemo.models;

import in.co.madhur.chatbubblesdemo.model.Status;
import in.co.madhur.chatbubblesdemo.model.UserType;

/**
 * Created by ckibuchi on 2/18/16.
 */
public class Comment {

    private String from, comment,pageTitle;
    private String to;
    private boolean isSelf;
    private String commentTime;
    private boolean sent = false;
    private boolean delivered = false;

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    private String commentID;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    private UserType userType;

    public Status getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(Status commentStatus) {
        this.commentStatus = commentStatus;
    }

    private Status commentStatus;

    public Comment() {
    }

    public Comment(String fromName, String comment, String commentTime, boolean isSelf, String to) {
        this.from = fromName;
        this.comment = comment;
        this.isSelf = isSelf;
        this.commentTime = commentTime;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
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

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
