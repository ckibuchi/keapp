package in.co.madhur.chatbubblesdemo.auth;

/**
 * Created by rube on 5/24/15.
 */


public class AccountGeneral {

    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "keapp.com";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "Keapp";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an keapp account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "FULL ACCESS";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an keapp account";

    public static final ServerAuthenticate sServerAuthenticate = new KeappServerAuthenticate();
}
