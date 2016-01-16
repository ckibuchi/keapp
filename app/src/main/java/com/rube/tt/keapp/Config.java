package com.rube.tt.keapp;

/**
 * Created by ckibuchi on 1/8/16.
 */

public interface Config {

    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "http://104.236.71.65:8080/KEapp-Server/GCMNotification?shareRegId=1";

    // GCM server using java
    // static final String APP_SERVER_URL =
    // "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "888304030943";
    static final String MESSAGE_KEY = "message";

}
