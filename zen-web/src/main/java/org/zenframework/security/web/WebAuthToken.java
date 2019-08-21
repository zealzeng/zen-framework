package org.zenframework.security.web;

import org.zenframework.util.StringUtils;
import org.zenframework.security.RememberMeAuthToken;

/**
 * Created by Zeal on 2019/1/13 0013.
 */
public class WebAuthToken implements RememberMeAuthToken {

    //Account name
    private String userName = null;

    //Account password
    private String password = null;

    //Captcha from http request
    private String requestCaptcha = null;

    //Required captcha in request, session or persistent store
    private String requiredCaptcha = null;

    //User checks remember-me or not, while using coolie to login, ignore this parameter
    private boolean rememberMe = false;

    //Remember me cookie token
    private String rememberMeToken = null;

    //Remember me cookie series
    private String rememberMeSeries = null;

    public String getRememberMeToken() {
        return rememberMeToken;
    }

    public void setRememberMeToken(String rememberMeToken) {
        this.rememberMeToken = rememberMeToken;
    }

    public String getRememberMeSeries() {
        return rememberMeSeries;
    }

    public void setRememberMeSeries(String rememberMeSeries) {
        this.rememberMeSeries = rememberMeSeries;
    }

    public String getRequestCaptcha() {
        return requestCaptcha;
    }

    public void setRequestCaptcha(String requestCaptcha) {
        this.requestCaptcha = requestCaptcha;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequiredCaptcha() {
        return requiredCaptcha;
    }

    public void setRequiredCaptcha(String requiredCaptcha) {
        this.requiredCaptcha = requiredCaptcha;
    }

    @Override
    public String getPrincipal() {
        return this.userName;
    }

    @Override
    public String getCredentials() {
        return this.password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public boolean isUsernamePasswordAuthentication() {
        return StringUtils.isNotEmpty(this.userName) && StringUtils.isNotEmpty(this.password);
    }

    public boolean isRememberMeAuthentication() {
        return StringUtils.isNotEmpty(this.rememberMeSeries) && StringUtils.isNotEmpty(this.rememberMeToken);
    }
}
