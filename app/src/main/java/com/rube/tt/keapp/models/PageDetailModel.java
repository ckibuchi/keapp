package com.rube.tt.keapp.models;

import android.graphics.Bitmap;

/**
 * Created by rube on 4/8/15.
 */
public class PageDetailModel {


    private String profileName;
    private Bitmap profilePic;
    private String title;
    private String date;
    private String text;
    private String likes;
    private String comments;


    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String name) {
        this.profileName = name;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap pic) {
        this.profilePic = pic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    @Override
    public String toString(){

        return  this.getText();

    }

    public String getLikes() {
        if(likes ==null)
            return "0";
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        if(comments == null)
            return "0";
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
