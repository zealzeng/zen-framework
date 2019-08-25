package org.zenframework.security;

import org.zenframework.common.Result;
import org.zenframework.security.annotation.AuthenticationRequest;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Zeal on 2019/1/9 0009.
 */
public interface AccessControl<A extends AuthenticationToken,B extends AuthInfo> {

    //Interceptor returns true
    int SKIP_SUCCESS = 0;

    //Interceptor returns false
    int SKIP_FAIL = -1;

    //Go to next step of interceptor
    int GO_AHEAD = 1;

    /**
     * Annotation class to enable access control
     *
     * @return
     */
    Class getEnableAnnotationClass();

    /**
     * After @RequireGuest
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws Exception
     */
    int postHandleRequireGuest(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    /**
     * After @RequireUser
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws Exception
     */
    int postHandleRequireUser(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    /**
     * After @RequireRole
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws Exception
     */
    int postHandleRequireRole(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    /**
     * After @RequirePermission
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws Exception
     */
    int postHandleRequirePermission(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    /**
     * Get user authenticated info for current request or session
     *
     * @param request
     * @return
     */
    B getAuthenticatedInfo(HttpServletRequest request);

    /**
     * Set user authenticated info for current request or session
     * @param request
     * @param authInfo
     */
    void setAuthenticatedInfo(HttpServletRequest request, B authInfo);

    /**
     * Create authentication token to login or auto-login
     * @param request
     * @param authenticateRequest Login with @AuthenticationRequest or invoke login manually
     * @return Null if it's not auth token, can Result can contain error code
     */
    Result<A> createAuthenticationToken(HttpServletRequest request, boolean authenticateRequest);

//    /**
//     * Validate auth token
//     * @param authToken
//     * @return
//     */
//    int validateAuthenticationToken(A authToken);

    /**
     * Get auth service to call authenticate()
     * @return
     */
    AuthService<A,B> getAuthService();

    /**
     * verifyAuthentication  result
     * @param request
     * @param response
     * @param handlerMethod
     * @param authResult
     * @return
     */
    int verifyAuthResult(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, AuthResult<A,B> authResult);

    /**
     * Authenticate success callback
     * @param request
     * @param response
     */
    void onAuthenticateSuccess(HttpServletRequest request, HttpServletResponse response,
                               AuthenticationRequest authenticationRequest, AuthResult<A,B> authResult);


    /**
     * Authenticate failure callback
     * @param request
     * @param response
     * @param errorCode
     * @param  authToken It can be null
     */
    void onAuthenticateFailure(HttpServletRequest request, HttpServletResponse response,
                               AuthenticationRequest authenticationRequest, A authToken, int errorCode) throws Exception;

    /**
     * Validate @RequireUser failure callback, can not find auth info from request or session
     * @param request
     * @param response
     */
    void onRequireUserFailure(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;

    /**
     * Validate @RequireRole failure callback, user has no role
     * @param request
     * @param response
     */
    void onRequireRoleFailure(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, int errorCode) throws Exception;

    /**
     * Validate @RequirePermission failure callback, user has no role
     * @param request
     * @param response
     */
    void onRequirePermissionFailure(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, int errorCode) throws Exception;

    /**
     * Do logout
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response);
}
