package org.zenframework.security;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Use AuthService and RememberMeService to authenticate
 * Created by Zeal on 2019/4/19 0019.
 */
public abstract class AbstractAccessControl<A extends AuthenticationToken,B extends AuthInfo> implements AccessControl<A,B> {

    //AuthService implementation
    protected AuthService<A,B> authService = null;

    /**
     * @param authService
     */
    public void setAuthService(AuthService<A,B> authService) {
        this.authService = authService;
    }

    /**
     * verifyAuthentication  result
     * @param request
     * @param response
     * @param handlerMethod
     * @param authResult
     * @return
     */
    public int verifyAuthResult(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, AuthResult<A,B> authResult) {
        return authResult.getResultCode();
    }

    /**
     * Get auth service
     * @return
     */
    @Override
    public AuthService<A,B> getAuthService() {
        return this.authService;
    }

    @Override
    public int postHandleRequireGuest(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        return AccessControl.GO_AHEAD;
    }

    @Override
    public int postHandleRequireUser(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        return AccessControl.GO_AHEAD;
    }

    @Override
    public int postHandleRequireRole(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        return AccessControl.GO_AHEAD;
    }

    @Override
    public int postHandleRequirePermission(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        return AccessControl.GO_AHEAD;
    }
}
