package org.zenframework.security;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zeal on 2019/4/25 0025.
 */
public class AuthResult<A extends AuthenticationToken,B extends AuthInfo> {

    private A authenticationToken = null;

    private int resultCode = AuthError.NO_ERROR;

    private B authInfo = null;

    private RememberMeToken rememberMeToken = null;

    private Map<Object,Object> resultMap = null;

    public RememberMeToken getRememberMeToken() {
        return rememberMeToken;
    }

    public void setRememberMeToken(RememberMeToken rememberMeToken) {
        this.rememberMeToken = rememberMeToken;
    }

    public AuthResult() {
    }

    public AuthResult(int resultCode) {
        this.setResultCode(resultCode);
    }

    public A getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(A authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public B getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(B authInfo) {
        this.authInfo = authInfo;
    }

    public void setResult(Object key, Object value) {
        if (this.resultMap == null) {
            this.resultMap = new HashMap<>();
        }
        this.resultMap.put(key, value);
    }

    public <T> void setResult(Class<T> clazz, Object value) {
        setResult(clazz, value);
    }

    public <T>T getResult(Class<T> clazz) {
        return (T) getResult((Object) clazz);
    }

    public Object getResult(Object key) {
        if (this.resultMap == null) {
            return null;
        }
        else {
            return this.resultMap.get(key);
        }
    }


}
