package org.zenframework.security.web;

import org.zenframework.security.AuthError;

/**
 * Created by Zeal on 2019/4/22 0022.
 */
public class WebAuthError extends AuthError {

    public static final int INVALID_SESSION = 498;

    //Need to show captcha, but session one is not valid
    public static final int INVALID_REQUIRED_CAPTCHA = 949;

    //Need to show captcha, but request one is not valid
    public static final int INVALID_REQUEST_CAPTCHA = 948;

    //Need to show captcha as wrong credential
    public static final int  INVALID_CREDENTIAL_CAPTCHA = 947;

    public static final int REMEMBER_ME_TOKEN_NOT_EXIST = 946;

    public static final int REMEMBER_ME_TOKEN_EXPIRED = 945;

    public static final int INVALID_REMEMBER_ME_TOKEN = 944;

    public static String getErrorMessage(int errorCode) {
        if (errorCode == INVALID_SESSION) {
            return "会话超时,请重新登录";
        }
        else if (errorCode == ACCESS_DENIED) {
            return "拒绝采访";
        }
        else if (errorCode == OTHER_AUTHC_ERROR) {
            return "登录失败";
        }
        else if (errorCode == INVALID_AUTHC_TOKEN) {
            return "登录信息有误";
        }
        else if (errorCode == GET_AUTHC_INFO_FAIL) {
            return "获取账号信息失败";
        }
        else if (errorCode == ACCOUNT_NOT_EXIST) {
            return "登录账号不存在";
        }
        else if (errorCode == ACCOUNT_LOCKED) {
            return "登录账号被锁定";
        }
        else if (errorCode == ACCOUNT_EXPIRED) {
            return "登录账号失效";
        }
        else if (errorCode == CREDENTIAL_EXPIRED) {
            return "登录账号密码失效";
        }
        else if (errorCode == INVALID_CREDENTIAL) {
            return "登录账号密码有误";
        }
        else if (errorCode == INVALID_PRINCIPAL) {
            return "登录账号有误";
        }
        else {
            return "未知登录错误";
        }
    }



}
