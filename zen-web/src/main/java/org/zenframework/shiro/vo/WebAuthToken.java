package org.zenframework.shiro.vo;

import org.zenframework.util.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by Zeal on 2019/1/13 0013.
 */
public class WebAuthToken extends UsernamePasswordToken {

    private String requestCaptcha = null;

    private String sessionCaptcha = null;

    //=================================================
    //Remember me cookie

    private String rememberMeToken = null;

    private String rememberMeSeries = null;

    public String getRequestCaptcha() {
        return requestCaptcha;
    }

    public void setRequestCaptcha(String requestCaptcha) {
        this.requestCaptcha = requestCaptcha;
    }

    public String getSessionCaptcha() {
        return sessionCaptcha;
    }

    public void setSessionCaptcha(String sessionCaptcha) {
        this.sessionCaptcha = sessionCaptcha;
    }

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

    public boolean validate() {

        if (StringUtils.isNotEmpty(this.getUsername()) && this.getPassword() != null) {
            return true;
        }
        else if (StringUtils.isNotEmpty(this.rememberMeToken) && StringUtils.isNotEmpty(this.rememberMeSeries)) {
            return true;
        }
        else {
            return false;
        }
    }
}
