package org.zenframework.security;

import org.zenframework.common.Result;
import org.zenframework.security.annotation.*;
import org.zenframework.security.util.AuthUtils;
import org.zenframework.web.error.WebError;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Zeal on 2019/1/8 0008.
 */
public class AccessControlInterceptor extends HandlerInterceptorAdapter {

    private Map<Class<? extends Annotation>, AccessControl> accessControlMap = new LinkedHashMap<>();

    private AccessControl defaultAccessControl = null;

    /**
     * Set access controls
     *
     * @param defaultAccessControl
     * @return
     */
    public AccessControlInterceptor setAccessControls(AccessControl defaultAccessControl, AccessControl... otherAccessControls) {

        Assert.notNull(defaultAccessControl, "Default access control is required");
        this.defaultAccessControl = defaultAccessControl;
        accessControlMap.put(defaultAccessControl.getEnableAnnotationClass(), defaultAccessControl);

        if (otherAccessControls != null && otherAccessControls.length > 0) {
            for (AccessControl accessControl : otherAccessControls) {
                if (accessControl == null) {
                    continue;
                }
                accessControlMap.put(accessControl.getEnableAnnotationClass(), accessControl);
            }
        }
        return this;
    }

    /**
     * Find annotation in method header first, and then find in class header
     *
     * @param handlerMethod
     * @param annotationClass
     * @param findInClass     find annotation in class header or not
     * @return
     */
    protected <A extends Annotation> A getHandlerMethodAnnotation(HandlerMethod handlerMethod, Class<A> annotationClass, boolean findInClass) {
        A up = handlerMethod.getMethodAnnotation(annotationClass);
        if (up != null || !findInClass) {
            return up;
        }
        Class<?> ctrlClass = handlerMethod.getMethod().getDeclaringClass();
        return AnnotationUtils.findAnnotation(ctrlClass, annotationClass);
    }


    /**
     * Pre handle
     *
     * @param request
     * @param response
     * @param handler
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = null;
        //Controller link
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
            return this.handleMethod(request, response, handlerMethod);
        }
        //HttpRequestHandler like DefaultHttpRequestHandler for static resources
        else if (handler instanceof HttpRequestHandler) {
            HttpRequestHandler requestHandler = (HttpRequestHandler) handler;
            return this.preHttpRequestHandler(request, response, requestHandler);
        }
        //Handle other unknown handler object
        else {
            return this.preObjectHandler(request, response, handler);
        }
    }

    /**
     * Get current access control
     * @param handlerMethod
     * @return
     */
    private AccessControl getAccessControl(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Class controllerClass = method.getDeclaringClass();
        AccessControl accessControl = null;
        //Only one access control
        if (this.accessControlMap.size() == 1) {
            accessControl = this.defaultAccessControl;
        } else {
            //Find the first access control
            for (Map.Entry<Class<? extends Annotation>, AccessControl> entry : this.accessControlMap.entrySet()) {
                Class<? extends Annotation> key = entry.getKey();
                if (controllerClass.isAnnotationPresent(key)) {
                    accessControl = entry.getValue();
                    break;
                }
            }
            //If access control is not present, use the default one
            if (accessControl == null) {
                accessControl = this.defaultAccessControl;
            }
        }
        return accessControl;
    }

    /**
     * Handle method, override it if @PublicPage,@UserPage,@AuthorityPage are not suitable for you
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws Exception
     */
    protected boolean handleMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        if (this.accessControlMap.size() == 0) {
            return true;
        }
        AccessControl accessControl = this.getAccessControl(handlerMethod);
        if (accessControl == null || accessControl.getAuthService() == null) {
            return true;
        }
        AuthUtils.setAccessControl(accessControl);

        //FIXME auto login/remember me login
        //Handle @AuthenticationRequest
        AuthenticationRequest loginRequest = this.getHandlerMethodAnnotation(handlerMethod, AuthenticationRequest.class, false);
        //Do login
        if (loginRequest != null) {
            this.authenticate(request, response,handlerMethod, accessControl, loginRequest);
            return true;
        }

        AuthInfo  authInfo = AuthUtils.getAuthInfo(request);
        if (authInfo == null) {
            this.authenticate(request, response, handlerMethod, accessControl, null);
        }

        //Public page, all users can access
        RequireGuest requireGuest = this.getHandlerMethodAnnotation(handlerMethod, RequireGuest.class, true);
        if (requireGuest != null) {
            return true;
        }
        //After @RequireGuest
        int result = accessControl.postHandleRequireGuest(request, response, handlerMethod);
        if (result == AccessControl.SKIP_SUCCESS) {
            return true;
        } else if (result == AccessControl.SKIP_FAIL) {
            return false;
        }

        //@RequireUser, we require user to login no matter method contains @RequireUser or not,
        RequireUser requireUser = this.getHandlerMethodAnnotation(handlerMethod, RequireUser.class, true);
        authInfo = accessControl.getAuthenticatedInfo(request);
        if (authInfo == null) {
            accessControl.onRequireUserFailure(request, response, handlerMethod);
            return false;
        }

        //After @RequireUser
        result = accessControl.postHandleRequireUser(request, response, handlerMethod);
        if (result == AccessControl.SKIP_SUCCESS) {
            return true;
        } else if (result == AccessControl.SKIP_FAIL) {
            return false;
        }

        //Check whether user has role
        RequireRole requireRole = this.getHandlerMethodAnnotation(handlerMethod, RequireRole.class, true);
        if (requireRole != null) {
            if (!AuthUtils.containsRole(authInfo, requireRole)) {
                accessControl.onRequireRoleFailure(request, response, handlerMethod, AuthError.ACCESS_DENIED);
                return false;
            }
        }
        //After @RequireRole
        result = accessControl.postHandleRequireRole(request, response, handlerMethod);
        if (result == AccessControl.SKIP_SUCCESS) {
            return true;
        } else if (result == AccessControl.SKIP_FAIL) {
            return false;
        }

        //Check whether user has permission
        RequirePermission requirePermission = this.getHandlerMethodAnnotation(handlerMethod, RequirePermission.class, true);
        if (requirePermission != null) {
            if (!AuthUtils.containsPermission(authInfo, requirePermission)) {
                accessControl.onRequirePermissionFailure(request, response, handlerMethod, AuthError.ACCESS_DENIED);
                return false;
            }
        }
        //After @RequirePermission
        result = accessControl.postHandleRequirePermission(request, response, handlerMethod);
        return result == AccessControl.SKIP_SUCCESS || result == AccessControl.GO_AHEAD;
    }

    /**
     * Do login
     * @param request
     * @param accessControl
     * @return
     */
    protected AuthResult authenticate(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
            AccessControl accessControl, AuthenticationRequest authenticationRequest) throws Exception {

        AuthResult authResult = new AuthResult();
        try {
            Result<AuthenticationToken> result = accessControl.createAuthenticationToken(request, authenticationRequest != null);
            if (result == null) {
                authResult.setResultCode(AuthError.INVALID_AUTHC_TOKEN);
                return authResult;
            } else if (result.getResultCode() != AuthError.NO_ERROR) {
                authResult.setResultCode(result.getResultCode());
                return authResult;
            }
            AuthenticationToken authToken = result.getResultEntity();
            if (authToken == null) {
                accessControl.onAuthenticateFailure(request, response, authenticationRequest, authToken, AuthError.INVALID_AUTHC_TOKEN);
                authResult.setResultCode(AuthError.INVALID_AUTHC_TOKEN);
                return authResult;
            }
            authResult = accessControl.getAuthService().authenticate(authToken);
            if (authResult.getResultCode() != WebError.NO_ERROR) {
                accessControl.onAuthenticateFailure(request, response, authenticationRequest, authToken, authResult.getResultCode());

            } else {
                int resultCode = accessControl.verifyAuthResult(request, response, handlerMethod, authResult);
                if (resultCode != AuthError.NO_ERROR) {
                    authResult.setResultCode(resultCode);
                    accessControl.onAuthenticateFailure(request, response, authenticationRequest, authToken, resultCode);
                }
                else {
                    accessControl.setAuthenticatedInfo(request, authResult.getAuthInfo());
                    accessControl.onAuthenticateSuccess(request, response, authenticationRequest, authResult);
                }
            }
            return authResult;
        }
        finally {
            AuthUtils.setAuthResult(authResult);
        }

    }

    /**
     * Override this method to handle HttpRequestHandler like static resources
     *
     * @param request
     * @param response
     * @param requestHandler
     * @return
     */
    protected boolean preHttpRequestHandler(HttpServletRequest request, HttpServletResponse response, HttpRequestHandler requestHandler) {
        return true;
    }

    /**
     * Override this method to handle other unknown object handler
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    protected boolean preObjectHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

}
