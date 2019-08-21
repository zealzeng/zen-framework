///*
// * Copyright (c) 2017, All rights reserved.
// */
//package org.zenframework.service;
//
//import org.zenframework.vo.Result;
//import org.zenframework.web.vo.AccessToken;
//import org.zenframework.web.vo.UserDetail;
//
///**
// * FIXME The package name is good enough, try to move to com.luoying.framework.oauth2 instead
// * @author Zeal
// * @since 2017年5月26日
// * @deprecated
// */
//public interface OAuth2Service {
//
//	/**
//	 * Check whether user is valid or not
//	 * @return 0 is valid user, others are invalid
//	 */
//	public Result<UserDetail> authorizeUser(String userName, String password);
//
//	/**
//	 * Get access token record by access_token
//	 * @param accessToken
//	 * @return
//	 */
//	public AccessToken getAccessToken(String accessToken);
//
//	/**
//	 * @param uuid
//	 * @return
//	 */
//	public AccessToken getAccessTokenByUuid(String uuid, String userId);
//
//	/**
//	 * @return
//	 */
//	public AccessToken getAccessTokenByRefreshToken(String refreshToken);
//
//	/**
//	 * @param accessToken
//	 * @return
//	 */
//	public int addAccessToken(AccessToken accessToken);
//
//	/**
//	 * @param accessToken
//	 * @return
//	 */
//	public int updateAccessToken(AccessToken accessToken);
//}
