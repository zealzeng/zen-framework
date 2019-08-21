/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.web.filter;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.web.method.HandlerMethod;
//
//import org.zenframework.core.util.CryptoUtils;
//import org.zenframework.core.util.DateUtils;
//import org.zenframework.core.util.StringUtils;
//import org.zenframework.core.vo.Result;
//import org.zenframework.service.OAuth2Service;
//import org.zenframework.web.annotation.TokenPage;
//import org.zenframework.web.util.OAuth2RequestContext;
//import org.zenframework.web.util.OAuth2Utils;
//import org.zenframework.web.util.WebUtils;
//import org.zenframework.web.vo.AccessToken;
//import org.zenframework.web.vo.ErrorCode;


/**
 * @author Zeal 2017年5月26日
 * @deprecated
 */
public class OAuth2AccessControlInterceptor extends AccessControlInterceptor {
	
//	private String tokenUri = "/oauth/token";
//
//	//Sign validity time, 5 mins
//	private long signValidityTime = 60000*5;
//
//	//OAuth2Service
//	private OAuth2Service oauth2Service = null;
//
//
//	/**
//	 * @return the tokenUri
//	 */
//	public String getTokenUri() {
//		return tokenUri;
//	}
//
//	/**
//	 * @param tokenUri the tokenUri to set
//	 */
//	public void setTokenUri(String tokenUri) {
//		this.tokenUri = tokenUri;
//	}
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
//
//	/**
//	 * After public page
//	 * @param request
//	 * @param response
//	 * @param handlerMethod
//	 * @return
//	 */
//	protected int postHandlePublicPage(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
//
//		TokenPage tokenPage = this.getMethodPageAnnotation(handlerMethod, TokenPage.class);
//		if (tokenPage != null) {
//			String accessTokenStr = request.getParameter("access_token");
//			String sign = request.getParameter("sign");
//			if (StringUtils.isBlank(accessTokenStr) || StringUtils.isBlank(sign)) {
//				WebUtils.jsonResult(response, ErrorCode.INVALID_OAUTH_REQUEST, "accesss_token和sign为必填项", null);
//				return SKIP_FAIL;
//			}
//			AccessToken accessToken = this.oauth2Service.getAccessToken(accessTokenStr);
//			if (accessToken == null) {
//				WebUtils.jsonResult(response, ErrorCode.INVALID_OAUTH_REQUEST, "accesss_token不存在", null);
//				return SKIP_FAIL;
//			}
//
//
//			Result<String> result = OAuth2Utils.validateAccessToken(accessToken, sign, signValidityTime);
//			if (result.getResultCode() != ErrorCode.NO_ERROR) {
//				WebUtils.jsonResult(response, result);
//				return SKIP_FAIL;
//			}
//			OAuth2RequestContext.setUserId(accessToken.getUserId());
//			return SKIP_SUCCESS;
//		}
//
//		return GO_AHEAD;
//	}
	

	
}
