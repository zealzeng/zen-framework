package org.zenframework.security;

/**
 * Created by Zeal on 2019/4/22 0022.
 */
public class AuthError {

    public static final String ERROR_CODE = "errorCode";

    public static final String ERROR_KEY_PREFIX = "err";

    public static final String ERROR_MESG = "error";

    public static final int NO_ERROR = 0;

    //4xx, http error code for client
    public static final int ACCESS_DENIED = 499;

    //999-950
    public static final int  OTHER_AUTHC_ERROR = 999;

    public static final int INVALID_AUTHC_TOKEN = 998;

    public static final int GET_AUTHC_INFO_FAIL = 997;

    public static final int ACCOUNT_NOT_EXIST = 996;

    public static final int ACCOUNT_LOCKED = 995;

    public static final int ACCOUNT_EXPIRED = 994;

    public static final int CREDENTIAL_EXPIRED = 993;

    public static final int  INVALID_CREDENTIAL = 992;

    public static final int  INVALID_PRINCIPAL = 991;

//    public static final int  INVALID_REMEMBER_ME_TOKEN = 990;



}
