/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.web.ctrl;

//import java.util.Date;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import org.zenframework.core.util.DateUtils;
//import org.zenframework.core.util.StringUtils;
//import org.zenframework.core.vo.Result;
//import org.zenframework.service.OAuth2Service;
//import org.zenframework.web.annotation.PublicPage;
//import org.zenframework.web.util.OAuth2Utils;
//import org.zenframework.web.util.WebUtils;
//import org.zenframework.web.vo.AccessToken;
//import org.zenframework.web.vo.ErrorCode;
//import org.zenframework.web.vo.UserDetail;
//
///**
// * @author Zeal
// * @since 2017年5月26日
// */
//@PublicPage
public class OAuth2Ctrl {
	
//	private OAuth2Service oauth2Service = null;
//
//	private long tokenValidityTime = 60000*60*24*7;
//
//	private long signValidityTime = 6000*5;
//
//	/**
//	 * @return the oauth2Service
//	 */
//	public OAuth2Service getOauth2Service() {
//		return oauth2Service;
//	}
//
//	/**
//	 * @param oauth2Service the oauth2Service to set
//	 */
//	public void setOauth2Service(OAuth2Service oauth2Service) {
//		this.oauth2Service = oauth2Service;
//	}
//
//	/**
//	 * Get access token
//	 * @param request
//	 * @return
//	 */
//	@ResponseBody
//	public String token(HttpServletRequest request) {
//		String grantType = request.getParameter("grant_type");
//		if (StringUtils.isEmpty(grantType)) {
//			return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "grant_type授权类型必填", null);
//		}
//		if (!AccessToken.GRANT_TYPE_PASSWORD.equals(grantType) && !AccessToken.GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
//			return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "grant_type授权类型当前只支持password,refresh_token", null);
//		}
//		//access token
//		if (AccessToken.GRANT_TYPE_PASSWORD.equals(grantType)) {
//			return this.accessToken(request);
//		}
//		//refresh token
//		else {
//			return this.refreshToken(request);
//		}
//	}
//
//	/**
//	 * @param request
//	 * @return
//	 */
//	private String accessToken(HttpServletRequest request) {
//		String uuid = request.getParameter("uuid");
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");
//		if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
//			return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "uuid,username,password为必填", null);
//		}
//		Result<UserDetail> result = this.oauth2Service.authorizeUser(username, password);
//		if (result.getResultCode() != ErrorCode.NO_ERROR) {
//			return WebUtils.jsonResult(result.getResultCode(), result.getResultMessage(), null);
//		}
//
//		UserDetail userDetail = result.getResultEntity();
//		AccessToken dbToken = this.oauth2Service.getAccessTokenByUuid(uuid, userDetail._userId());
//		Date now = new Date();
////		AccessToken accessToken = dbToken == null ? new AccessToken() : dbToken;
////		accessToken.setAccessToken(StringUtils.generateUUID());
////		accessToken.setAccessTime(now);
////		accessToken.setRefreshToken(StringUtils.generateUUID());
////		accessToken.setExpireTime(DateUtils.addMilliseconds(now, (int)this.tokenValidityTime));
////		accessToken.setUuid(uuid);
////		accessToken.setUserId(userDetail._userId());
//		//Insert
//		if (dbToken == null) {
//			dbToken = new AccessToken();
//			dbToken.setAccessToken(StringUtils.generateUUID());
//			dbToken.setAccessTime(now);
//			dbToken.setRefreshToken(StringUtils.generateUUID());
//			dbToken.setExpireTime(DateUtils.addMilliseconds(now, (int)this.tokenValidityTime));
//			dbToken.setUuid(uuid);
//			dbToken.setUserId(userDetail._userId());
//			this.oauth2Service.addAccessToken(dbToken);
//		}
//		//Update
//		else {
////			if (!dbToken.getUserId().equals(userDetail._userId())) {
////				return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "userId不一致", null);
////			}
//			dbToken.setAccessToken(StringUtils.generateUUID());
//			dbToken.setAccessTime(now);
//			dbToken.setRefreshToken(StringUtils.generateUUID());
//			dbToken.setExpireTime(DateUtils.addMilliseconds(now, (int)this.tokenValidityTime));
//			this.oauth2Service.updateAccessToken(dbToken);
//		}
//
//
//		return WebUtils.jsonResult(ErrorCode.NO_ERROR, "采访令牌环获取成功", dbToken);
//	}
//
//	/**
//	 * @param request
//	 * @return
//	 */
//	private String refreshToken(HttpServletRequest request) {
//		String refreshToken = request.getParameter("refresh_token");
//		if (StringUtils.isEmpty(refreshToken)) {
//			return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "refresh_token为必填", null);
//		}
//		String sign = request.getParameter("sign");
//		if (StringUtils.isEmpty(sign)) {
//			return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "sign为必填", null);
//		}
//		AccessToken accessToken = this.oauth2Service.getAccessTokenByRefreshToken(refreshToken);
//		if (accessToken == null) {
//			return WebUtils.jsonResult(ErrorCode.INVALID_OAUTH_REQUEST, "refresh_token无记录", null);
//		}
//		Result<String> result = OAuth2Utils.validateAccessToken(accessToken, sign, signValidityTime);
//		if (result.getResultCode() != ErrorCode.NO_ERROR) {
//			return WebUtils.jsonResult(result);
//		}
//		Date now = new Date();
//		accessToken.setAccessToken(StringUtils.generateUUID());
//		accessToken.setAccessTime(now);
//		accessToken.setRefreshToken(StringUtils.generateUUID());
//		accessToken.setExpireTime(DateUtils.addMilliseconds(now, (int)this.tokenValidityTime));
//		this.oauth2Service.updateAccessToken(accessToken);
//		return WebUtils.jsonResult(ErrorCode.NO_ERROR, "采访令牌环获取成功", accessToken);
//	}
//
//	/**
//	 * @return the tokenValidityTime
//	 */
//	public long getTokenValidityTime() {
//		return tokenValidityTime;
//	}
//
//	/**
//	 * @param tokenValidityTime the tokenValidityTime to set
//	 */
//	public void setTokenValidityTime(long tokenValidityTime) {
//		this.tokenValidityTime = tokenValidityTime;
//	}
//
//	/**
//	 * @return the signValidityTime
//	 */
//	public long getSignValidityTime() {
//		return signValidityTime;
//	}
//
//	/**
//	 * @param signValidityTime the signValidityTime to set
//	 */
//	public void setSignValidityTime(long signValidityTime) {
//		this.signValidityTime = signValidityTime;
//	}
	
}
