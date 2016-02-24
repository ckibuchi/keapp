package in.co.madhur.chatbubblesdemo.auth;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

/**
 * Created by rube on 5/24/15.
 */

public interface ServerAuthenticate {
    public String userSignUp(ArrayList<NameValuePair> nameValuePairs,String pass, String authType) throws Exception;
    public String userSignIn(final String user, final String pass, String authType) throws Exception;

}
