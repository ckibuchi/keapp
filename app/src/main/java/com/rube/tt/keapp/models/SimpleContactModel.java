package com.rube.tt.keapp.models;

import android.graphics.Bitmap;

/**
 * Created by rube on 4/8/15.
 */
public class SimpleContactModel {

    private Bitmap pic;
    private String name;
    private String phoneNumber;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString(){

        return  this.getName()+ " - " +this.getPhoneNumber();

    }
}
