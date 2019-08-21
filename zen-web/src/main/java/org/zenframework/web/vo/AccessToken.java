/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.web.vo;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Zeal
 * @since 2017年5月27日
 */
public class AccessToken implements Serializable {
	
	public static final String GRANT_TYPE_PASSWORD = "password";
	
	public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
	

	private static final long serialVersionUID = 8001637590364639480L;
	
	@JSONField(deserialize=false, serialize=false)
	private String uuid = null;
	
	@JSONField(deserialize=false, serialize=false)
	private String userId = null;
	
	@JSONField(name="access_token")
	private String accessToken = null;
	
	@JSONField(deserialize=false, serialize=false)
	private Date accessTime = null;
	
	@JSONField(deserialize=false, serialize=false)
	private Date expireTime = null;
	
	@JSONField(name="refresh_token")
	private String refreshToken = null;

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the accessTime
	 */
	public Date getAccessTime() {
		return accessTime;
	}

	/**
	 * @param accessTime the accessTime to set
	 */
	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}

	/**
	 * @return the expireTime
	 */
	public Date getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
	@JSONField(name="expires_in")
	public long getExpiresIn() {
		long start = System.currentTimeMillis();
		long end = this.expireTime == null ? 0 : this.expireTime.getTime();
		return (end - start) / 1000L;
	}
	

}
