package com.rube.tt.keapp.models;

import android.graphics.Bitmap;

/**
 * Created by rube on 4/8/15.
 */
public class PageModel {


    private String name;
    private Bitmap pic;
    private String date;
    private String category;
    private String subCategory;
    private String description;
    private String likes;
    private String comments;
    private String id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString(){

        return  this.getName()+ " - "+this.getCategory()+ "-" +this.getSubCategory();

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
