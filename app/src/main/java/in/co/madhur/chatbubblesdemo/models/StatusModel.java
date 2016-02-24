package in.co.madhur.chatbubblesdemo.models;

/**
 * Created by rube on 4/8/15.
 */
public class StatusModel {

    private String statusMessasge;

    public String getStatusMessasge() {
        return statusMessasge;
    }

    public void setStatusMessasge(String messasge) {
        this.statusMessasge = messasge;
    }

    @Override
    public String toString(){

        return  this.getStatusMessasge();

    }

}
