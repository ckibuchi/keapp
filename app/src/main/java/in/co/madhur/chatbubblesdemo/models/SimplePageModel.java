package in.co.madhur.chatbubblesdemo.models;

import android.graphics.Bitmap;

/**
 * Created by rube on 4/8/15.
 */
public class SimplePageModel {


    private String name;
    private Bitmap pic;
    private String date;
    private String category;
    private String subCategory;


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


    @Override
    public String toString(){

        return  this.getName()+ " - "+this.getCategory()+ "-" +this.getSubCategory();

    }

}
