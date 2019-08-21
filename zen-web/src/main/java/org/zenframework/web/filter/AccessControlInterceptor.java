/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.web.filter;

//import java.util.Collection;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.util.Assert;
//import org.springframework.web.HttpRequestHandler;
//import org.springframework.web.method.HandlerMethod;
//
//import org.zenframework.web.annotation.AuthorityPage;
//import org.zenframework.web.annotation.PublicPage;
//import org.zenframework.web.annotation.UserPage;
//import org.zenframework.web.util.SessionUtils;
//import org.zenframework.web.vo.ErrorCode;
//import org.zenframework.web.vo.UserDetail;

/**
 * Default access control interceptor for authorization.
 * 1. If ctrl or method contains @PublicPage, all users can access
 * 2. If 
 * @author Zeal since 2016年4月27日
 * @deprecated
 */
public class AccessControlInterceptor extends HandlerInterceptorSupport {
	
//	/** Save sth into session or request atribute to mark user is logined */
//	private String userDetailKey = "user_detail_key";
//
//	protected static final int SKIP_SUCCESS = 0;
//
//	protected static final int SKIP_FAIL = -1;
//
//	protected static final int GO_AHEAD = 1;
//
//	/**
//	 * Check whether controller or method is public
//	 * @param handlerMethod
//	 * @return
//	 */
//	private boolean isPublicPage(HandlerMethod handlerMethod) {
//		if (handlerMethod == null) {
//			return false;
//		}
//		//TODO Reserve the method/class as cache? which is faster?
//		//We had better to load all the @PublicPage when system is started
//		PublicPage pp = handlerMethod.getMethodAnnotation(PublicPage.class);
//		if (pp != null) {
//			return true;
//		}
//		Class<?> clazz = handlerMethod.getMethod().getDeclaringClass();
//		pp = AnnotationUtils.findAnnotation(clazz, PublicPage.class);
//		return pp != null;
//	}
//
//	/**
//	 * Check whether controller or method contain @UserPage
//	 * @param handlerMethod
//	 * @return
//	 */
//	private UserPage getUserPageAnnotation(HandlerMethod handlerMethod) {
//		return getMethodPageAnnotation(handlerMethod, UserPage.class);
//	}
//
//	/**
//	 * Check whether ctrl or method contain @AuthorityPage
//	 * @param handlerMethod
//	 * @return
//	 */
//	private AuthorityPage getAuthorityPageAnnocation(HandlerMethod handlerMethod) {
//		return getMethodPageAnnotation(handlerMethod, AuthorityPage.class);
//	}
//
//
//
//	/**
//	 * Check whether user contains role defined in @UserPage
//	 * @param userDetail
//	 * @param userPage
//	 * @return
//	 */
//	private boolean checkUserRole(UserDetail userDetail, UserPage userPage) {
//		Assert.notNull(userDetail, "userDetail is required");
//		Assert.notNull(userPage, "userPage is required");
//		String[] requiredRoles = userPage.roles();
//		//No role is required, logined user can access
//		if (requiredRoles == null || requiredRoles.length <= 0) {
//			return true;
//		}
//		Collection<String> userRoles = userDetail._roles();
//		//User contains no role
//		if (userRoles == null || userRoles.size() <= 0) {
//			return false;
//		}
//		//Check whether user contains one of the required role
//		for (String requiredRole : requiredRoles) {
//			if (userRoles.contains(requiredRole)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Check whether user owns authority to access
//	 * @param userDetail
//	 * @param authPage
//	 * @return
//	 */
//	private boolean checkAuthPage(UserDetail userDetail, AuthorityPage authPage) {
//		Assert.notNull(userDetail, "userDetail is required");
//		Assert.notNull(authPage, "authPage is required");
//		if (authPage.value().length <= 0) {
//			throw new IllegalStateException("@AuthorityPage value is required");
//		}
//		Collection<String> userAuthories = userDetail._authorities();
//		if (userAuthories == null || userAuthories.size() <= 0) {
//			return false;
//		}
//		for (String requiredAuth : authPage.value()) {
//			if (!userAuthories.contains(requiredAuth)) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * Get user detail from session or request
//	 * @param request
//	 * @return
//	 */
//	protected UserDetail getUserDetail(HttpServletRequest request) {
//		UserDetail detail = SessionUtils.getAttribute(request, this.userDetailKey, UserDetail.class);
//		if (detail == null) {
//			detail = (UserDetail) request.getAttribute(this.userDetailKey);
//		}
//		return detail;
//	}
//
//	/**
//	 * Acess control interceptor
//	 * @param request
//	 * @param response
//	 * @param handler
//	 */
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//		HandlerMethod handlerMethod = null;
//		//Controller link
//		if (handler instanceof HandlerMethod) {
//			handlerMethod = (HandlerMethod) handler;
//			return this.handleMethod(request, response, handlerMethod);
//		}
//		//HttpRequestHandler like DefaultHttpRequestHandler for static resources
//		else if (handler instanceof HttpRequestHandler) {
//			HttpRequestHandler requestHandler = (HttpRequestHandler) handler;
//			return this.preHttpRequestHandler(request, response, requestHandler);
//		}
//		//Handle other unknown handler object
//		else {
//			return this.preObjectHandler(request, response, handler);
//		}
//	}
//
//	/**
//	 * Handle method, override it if @PublicPage,@UserPage,@AuthorityPage are not suitable for you
//	 * @param request
//	 * @param response
//	 * @param handlerMethod
//	 * @return
//	 * @throws Exception
//	 */
//	protected boolean handleMethod(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
//
//		//Special process before @PublicPage
//		int ret = preHandleMethod(request, response, handlerMethod);
//		if (ret == SKIP_SUCCESS) {
//			return true;
//		}
//		else if (ret == SKIP_FAIL) {
//			return false;
//		}
//
//		//@PublicPage all users can access
//		if (this.isPublicPage(handlerMethod)) {
//			return true;
//		}
//		//After public page
//		ret = this.postHandlePublicPage(request, response, handlerMethod);
//		if (ret == SKIP_SUCCESS) {
//			return true;
//		}
//		else if (ret == SKIP_FAIL) {
//			return false;
//		}
//
//		//Exlclude @PublicPage, others require user to sign-in
//		UserDetail userDetail = this.getUserDetail(request);
//		if (userDetail == null) {
//			return this.userDetailError(request, response, handlerMethod, ErrorCode.INVALID_SESSION);
//		}
//		//After user detail
//		ret = this.postHandleUserDetail(request, response, handlerMethod);
//		if (ret == SKIP_SUCCESS) {
//			return true;
//		}
//		else if (ret == SKIP_FAIL) {
//			return false;
//		}
//
//		//@UserPage with role
//		UserPage userPage = this.getUserPageAnnotation(handlerMethod);
//		if (userPage != null) {
//			if (!this.checkUserRole(userDetail, userPage)) {
//				return this.userPageError(request, response, handlerMethod, ErrorCode.ACCESS_DENIED);
//			}
//		}
//		//After user page
//		ret = this.postHandleUserPage(request, response, handlerMethod);
//		if (ret == SKIP_SUCCESS) {
//			return true;
//		}
//		else if (ret == SKIP_FAIL) {
//			return false;
//		}
//
//		//@UserPage and @AuthorityPage can use together
//		//@AuthorityPage
//		AuthorityPage authPage = this.getAuthorityPageAnnocation(handlerMethod);
//		if (authPage != null) {
//			if (!this.checkAuthPage(userDetail, authPage)) {
//				return this.authPageError(request, response, handlerMethod, ErrorCode.ACCESS_DENIED);
//			}
//		}
//		//Override if there's additional validations
//		return this.postHandleMethod(request, response, handlerMethod, userDetail);
//	}
//
//	/**
//	 * Authority page error
//	 * @param request
//	 * @param response
//	 * @param errorCode
//	 * @return
//	 * @throws Exception
//	 */
//	protected boolean authPageError(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod method, int errorCode) throws Exception {
//		return this.error(request, response, method, errorCode);
//	}
//
//	/**
//	 * After user page
//	 * @param request
//	 * @param response
//	 * @param handlerMethod
//	 * @return
//	 */
//	protected int postHandleUserPage(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
//		return GO_AHEAD;
//	}
//
//	/**
//	 * User page error
//	 * @param request
//	 * @param response
//	 * @param errorCode
//	 * @return
//	 * @throws Exception
//	 */
//	protected boolean userPageError(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod method, int errorCode) throws Exception {
//		return this.error(request, response, method, errorCode);
//	}
//
//	/**
//	 * User detail not found in request or session
//	 * @param request
//	 * @param response
//	 * @param errorCode
//	 * @return
//	 * @throws Exception
//	 */
//	protected boolean userDetailError(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod method, int errorCode) throws Exception {
//		return this.error(request, response, method, errorCode);
//	}
//
//	/**
//	 * After user detail
//	 * @param request
//	 * @param response
//	 * @param handlerMethod
//	 * @return
//	 */
//	protected int postHandleUserDetail(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
//		return GO_AHEAD;
//	}
//
//
//	/**
//	 * Before public page
//	 * @param request
//	 * @param response
//	 * @param handleMethod
//	 * @param userDetail
//	 * @return
//	 */
//	protected int preHandleMethod(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod) {
//		return GO_AHEAD;
//	}
//
//	/**
//	 * After public page
//	 * @param request
//	 * @param response
//	 * @param handleMethod
//	 * @param userDetail
//	 * @return
//	 */
//	protected int postHandlePublicPage(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
//		return GO_AHEAD;
//	}
//
//	/**
//	 * After authority page
//	 * @param request
//	 * @param response
//	 * @param handleMethod
//	 * @param userDetail
//	 * @return
//	 */
//	protected boolean postHandleMethod(HttpServletRequest request,
//		HttpServletResponse response, HandlerMethod handlerMethod, UserDetail userDetail) throws Exception {
//		return true;
//	}
//
//	/**
//	 * Override this method to handle HttpRequestHandler like static resources
//	 * @param request
//	 * @param response
//	 * @param handleMethod
//	 * @param userDetail
//	 * @return
//	 */
//	protected boolean preHttpRequestHandler(HttpServletRequest request,
//		HttpServletResponse response, HttpRequestHandler requestHandler) {
//		return true;
//	}
//
//	/**
//	 * Override this method to handle other unknow object handler
//	 * @param request
//	 * @param response
//	 * @param handleMethod
//	 * @param userDetail
//	 * @return
//	 */
//	protected boolean preObjectHandler(HttpServletRequest request,
//		HttpServletResponse response, Object handler) {
//		return true;
//	}
//
//	/**
//	 * @return the userDetailKey
//	 */
//	public String getUserDetailKey() {
//		return userDetailKey;
//	}
//
//	/**
//	 * @param userDetailKey the userDetailKey to set
//	 */
//	public void setUserDetailKey(String userDetailKey) {
//		this.userDetailKey = userDetailKey;
//	}
//

}
