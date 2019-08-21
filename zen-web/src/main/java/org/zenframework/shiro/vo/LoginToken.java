package org.zenframework.shiro.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Zeal on 2019/1/25 0025.
 */
public class LoginToken implements RememberMeToken, Serializable {

    public static final int DELETED_NO = 0;

    public static final int DELETED_YES = 1;

    private int loginTokenId = 0;

    private int userId = 0;

    private String series = null;

    private String token = null;

    private Date lastTime = null;

    private int deleted = DELETED_NO;

    public int getLoginTokenId() {
        return loginTokenId;
    }

    public void setLoginTokenId(int loginTokenId) {
        this.loginTokenId = loginTokenId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    @Override
    public Object getAuthInfoKey() {
        return this.userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastTime() {
        return lastTime;
    }

    @Override
    public void touch(Object authInfoKey, String newToken, String newSeries, Date newLastTime) {
        if (authInfoKey != null) {
            this.userId = (Integer) authInfoKey;
        }
        this.token = newToken;
        this.series = newSeries;
        this.lastTime = newLastTime;
    }

    @Override
    public void clearSensitiveInfo() {
        this.loginTokenId = 0;
        this.userId = 0;
        this.deleted = DELETED_NO;
        this.lastTime = null;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
