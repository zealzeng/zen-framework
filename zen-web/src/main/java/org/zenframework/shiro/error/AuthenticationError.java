package org.zenframework.shiro.error;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Created by Zeal on 2019/1/13 0013.
 */
public class AuthenticationError extends AuthenticationException {

    public static final int INVALID_AUTHC_TOKEN = 999;

    public static final int GET_AUTHC_INFO_FAIL = 998;

    public static final int ACCOUNT_NOT_EXIST = 997;

    public static final int ACCOUNT_LOCKED = 996;

    public static final int ACCOUNT_EXPIRED = 995;

    public static final int CREDENTIAL_EXPIRED = 994;

    //Need to show captcha, but session one is not valid
    public static final int INVALID_SESSION_CAPTCHA = 993;

    //Need to show captcha, but request one is not valid
    public static final int INVALID_REQUEST_CAPTCHA = 992;

    public static final int  INVALID_CREDENTIAL = 991;

    //Need to show captcha as wrong credential
    public static final int  INVALID_CREDENTIAL_CAPTCHA = 990;

    public static final int REMEMBER_ME_TOKEN_NOT_EXIST = 989;

    public static final int REMEMBER_ME_TOKEN_EXPIRED = 988;

    public static final int AUTHC_CONFIG_ERROR = 987;

    public static final int  OTHER_AUTHC_ERROR = 986;

    private int errorCode = 0;

    public AuthenticationError(int errorCode) {
        this.errorCode = errorCode;
    }

    public AuthenticationError(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
