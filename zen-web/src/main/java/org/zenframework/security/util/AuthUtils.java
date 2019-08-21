package org.zenframework.security.util;

import org.zenframework.vo.Result;
import org.zenframework.security.*;
import org.zenframework.security.annotation.AuthenticationRequest;
import org.zenframework.security.annotation.Logical;
import org.zenframework.security.annotation.RequirePermission;
import org.zenframework.security.annotation.RequireRole;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zeal on 2019/4/18 0018.
 */
public class AuthUtils {

    private static final String ACCESS_CTRL_KEY = AuthUtils.class.getName() + "_ACCESS_CTRL_KEY";

    private static final String AUTH_INFO_KEY = AuthUtils.class.getName() + "_AUTH_INFO_KEY";

    private static final String AUTH_REQUEST_KEY = AuthUtils.class.getName() + "_AUTH_REQUEST_KEY";

    public static final String AUTH_RESULT_KEY = AuthUtils.class.getName() + "_AUTH_RESULT_KEY";
//
//    public static final String AUTH_TOKEN_KEY = AuthUtils.class.getName() + "_AUTH_TOKEN_KEY";

    private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<Map<Object, Object>>();

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * Check whether user contains role or not
     * @param authInfo
     * @param requireRole
     * @return
     */
    public static boolean containsRole(AuthInfo authInfo, RequireRole requireRole) {
        //No user info at all, return false
        if (authInfo == null) {
            return false;
        }
        //Has user info , but no role is required
        if (requireRole == null) {
            return true;
        }
        String[] requireRoles = requireRole.value();
        if (requireRoles == null || requireRoles.length <= 0) {
            return true;
        }
        //Require role but user has no role
        Set<String> userRoles = authInfo.getRoles();
        if (userRoles == null || userRoles.size() <= 0) {
            return false;
        }
        return logicalContain(userRoles, requireRoles, requireRole.logical());
    }

    /**
     * Logical contain, AND/OR
     * @param userValues
     * @param requireValues
     * @param logical
     * @return
     */
    public static boolean logicalContain(Set<String> userValues, String[] requireValues, Logical logical) {
        if (logical == Logical.AND) {
            for (String reqRole : requireValues) {
//                if (!userValues.contains(reqRole)) {
//                    return false;
//                }
                if (!wildcardContain(userValues, reqRole)) {
                    return false;
                }
            }
            return true;
        }
        //Or
        else {
            for (String reqRole : requireValues) {
//                if (userValues.contains(reqRole)) {
//                    return true;
//                }
                if (wildcardContain(userValues, reqRole)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean wildcardContain(Set<String> userValues, String requireValue) {
        for (String userValue : userValues) {
            if (antPathMatcher.match(requireValue, userValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether user contains permission or not
     * @param authInfo
     * @param requirePermission
     * @return
     */
    public static boolean containsPermission(AuthInfo authInfo, RequirePermission requirePermission) {
        //No user info at all, return false
        if (authInfo == null) {
            return false;
        }
        //Has user info , but no permission is required
        if (requirePermission == null) {
            return true;
        }
        String[] requirePermissions = requirePermission.value();
        if (requirePermissions == null || requirePermissions.length <= 0) {
            return true;
        }
        //Require role but user has no permission
        Set<String> userPermissions = authInfo.getPermissions();
        if (userPermissions == null || userPermissions.size() <= 0) {
            return false;
        }
        return logicalContain(userPermissions, requirePermissions, requirePermission.logical());
    }

    //===========================================================================================

    /**
     * Set access control into thread local
     * @param accessControl
     */
    public static void setAccessControl(AccessControl accessControl) {
        Map<Object,Object> map = getResourceMap();
        map.put(ACCESS_CTRL_KEY, accessControl);
    }

    private static Map<Object,Object> getResourceMap() {
        Map<Object,Object> map = resources.get();
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }
        return map;
    }

    /**
     * Get access control from thread local
     * @return
     */
    public static <A extends AuthenticationToken,B extends AuthInfo> AccessControl<A,B> getAccessControl() {
        Map<Object,Object> map = resources.get();
        return map == null ? null : (AccessControl<A,B>) map.get(ACCESS_CTRL_KEY);
    }

    public static <A extends AuthenticationToken,B extends AuthInfo> void setAuthResult(AuthResult<A,B> authResult) {
         Map<Object,Object> map = getResourceMap();
         map.put(AUTH_RESULT_KEY, authResult);
    }

    public static <A extends AuthenticationToken,B extends AuthInfo>AuthResult<A,B> getAuthResult() {
        Map<Object,Object> map = resources.get();
        return map == null ? null : (AuthResult<A,B>) map.get(AUTH_RESULT_KEY);
    }

    /**
     * Do login
     * @param request
     * @param response
     * @param <A>
     * @param <B>
     * @return
     * @throws Exception
     */
    public static <A extends AuthenticationToken,B extends AuthInfo>AuthResult<A,B> authenticate(
        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return authenticate(request, response, true, false);
    }

    public static AuthenticationRequest createAuthenticateRequest() {
        return new AuthenticationRequest() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return AuthenticationRequest.class;
            }
        };
    }

    /**
     * Do login
     * @param request
     * @return
     */
    public static <A extends AuthenticationToken,B extends AuthInfo>AuthResult<A,B> authenticate(
        HttpServletRequest request, HttpServletResponse response, boolean authSuccessCallback, boolean authFailureCallback) throws Exception {

        AccessControl<A,B> accessControl = AuthUtils.getAccessControl();
        if (accessControl == null || accessControl.getAuthService() == null) {
            return new AuthResult<>(AuthError.ACCESS_DENIED);
        }
        AuthenticationRequest authenticationRequest = createAuthenticateRequest();
        Result<A> result = accessControl.createAuthenticationToken(request, authenticationRequest != null);
        if (result == null) {
            if (authFailureCallback) {
                accessControl.onAuthenticateFailure(request, response, authenticationRequest, null, AuthError.INVALID_AUTHC_TOKEN);
            }
            return new AuthResult<>(AuthError.INVALID_AUTHC_TOKEN);
        }
        else if (result.getResultCode() != AuthError.NO_ERROR) {
            if (authFailureCallback) {
                accessControl.onAuthenticateFailure(request, response, authenticationRequest, result.getResultEntity(), result.getResultCode());
            }
            return new AuthResult<>(result.getResultCode());
        }
        A authToken = result.getResultEntity();
        AuthResult<A,B> authResult = accessControl.getAuthService().authenticate(authToken);
        if (authResult.getAuthenticationToken() == null) {
            authResult.setAuthenticationToken(authToken);
        }
        if (authResult.getResultCode() != AuthError.NO_ERROR) {
            if (authFailureCallback) {
                accessControl.onAuthenticateFailure(request, response, authenticationRequest, authToken, authResult.getResultCode());
            }
            return authResult;
        }
        else {
            //int resultCode =
            accessControl.setAuthenticatedInfo(request, authResult.getAuthInfo());
            if (authSuccessCallback) {
                accessControl.onAuthenticateSuccess(request, response, authenticationRequest, authResult);
            }
            return authResult;
        }
    }

        /**
     * Do login
     * @param request
     * @return
     */
    public static <A extends AuthenticationToken,B extends AuthInfo>AuthResult<A,B> authenticate(
        HttpServletRequest request, HttpServletResponse response, A authToken, boolean authSuccessCallback, boolean authFailureCallback) throws Exception {

        AccessControl<A,B> accessControl = AuthUtils.getAccessControl();
        if (accessControl == null || accessControl.getAuthService() == null) {
            return new AuthResult<>(AuthError.ACCESS_DENIED);
        }
        AuthenticationRequest authenticationRequest = createAuthenticateRequest();
        AuthResult<A,B> authResult = accessControl.getAuthService().authenticate(authToken);
        if (authResult.getAuthenticationToken() == null) {
            authResult.setAuthenticationToken(authToken);
        }
        if (authResult.getResultCode() != AuthError.NO_ERROR) {
            if (authFailureCallback) {
                accessControl.onAuthenticateFailure(request, response, authenticationRequest, authToken, authResult.getResultCode());
            }
            return authResult;
        }
        else {
            accessControl.setAuthenticatedInfo(request, authResult.getAuthInfo());
            if (authSuccessCallback) {
                accessControl.onAuthenticateSuccess(request, response, authenticationRequest, authResult);
            }
            return authResult;
        }
    }

    /**
     * Do logout
     * @param request
     * @param response
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        AccessControl accessControl = getAccessControl();
        if (accessControl != null) {
            accessControl.logout(request, response);
        }
    }

    /**
     * Get auth info from thread local
     * @param <T>
     * @return
     */
    public static <T extends AuthInfo>T getAuthInfo(HttpServletRequest request) {
        AccessControl accessControl = getAccessControl();
        if (accessControl == null) {
            return null;
        }
        else {
            return (T) accessControl.getAuthenticatedInfo(request);
        }
    }

//    /**
//     * Identify request is @AuthenticationRequest or not
//     * @param request
//     * @param authenticateRequest
//     */
//    public static void setAuthenticationRequest(HttpServletRequest request, boolean authenticateRequest) {
//        if (authenticateRequest) {
//            request.setAttribute(AUTH_REQUEST_KEY, authenticateRequest);
//        }
//        else {
//            request.removeAttribute(AUTH_REQUEST_KEY);
//        }
//    }
//
//    /**
//     * Get @AuthenticationRequest
//     * @param request
//     * @return
//     */
//    public static boolean getAuthenticationRequest(HttpServletRequest request) {
//        Boolean authRequest = (Boolean) request.getAttribute(AUTH_REQUEST_KEY);
//        return authRequest == null ? false : authRequest.booleanValue();
//    }

    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends InheritableThreadLocal<Map<Object, Object>> {

        /**
         * This implementation was added to address a
         * <a href="http://jsecurity.markmail.org/search/?q=#query:+page:1+mid:xqi2yxurwmrpqrvj+state:results">
         * user-reported issue</a>.
         * @param parentValue the parent value, a HashMap as defined in the {@link #initialValue()} method.
         * @return the HashMap to be used by any parent-spawned child threads (a clone of the parent HashMap).
         */
        @SuppressWarnings({"unchecked"})
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
            } else {
                return null;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Set<String> userValues = new HashSet<>();
        userValues.add("boss.abc");
        userValues.add("boss.123");
        boolean ret = wildcardContain(userValues, "boss.*");
        System.out.println(ret);
    }



}
