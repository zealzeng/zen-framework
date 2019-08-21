package org.zenframework.security.web;


import org.zenframework.security.RememberMeToken;

import java.util.Date;

/**
 * Created by Zeal on 2019/4/22 0022.
 */
public class WebRememberMeToken implements RememberMeToken {

    public static final int DELETED_NO = 0;

    public static final int DELETED_YES = 1;

    //Primary key
    private int tokenId = 0;

    private Integer userId = null;

    private String token = null;

    private String series = null;

    private Date lastTime = null;

    private int deleted = DELETED_NO;

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public Integer getAuthInfoKey() {
        return this.userId;
    }

    @Override
    public void setAuthInfoKey(Object key) {
        this.userId = (Integer) key;
    }

    @Override
    public Date getLastTime() {
        return null;
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

    @Override
    public String getRememberMeSeries() {
        return this.series;
    }

    @Override
    public void setRememberMeSeries(String series) {
        this.series = series;
    }

    @Override
    public String getRememberMeToken() {
        return this.token;
    }

    @Override
    public void setRememberMeToken(String token) {
        this.token = token;
    }
}
