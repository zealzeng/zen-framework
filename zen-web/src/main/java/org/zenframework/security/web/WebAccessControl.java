package org.zenframework.security.web;

import org.zenframework.captcha.CaptchaGenerator;
import org.zenframework.captcha.DefaultCaptchaGenerator;
import org.zenframework.util.StringUtils;
import org.zenframework.common.Result;
import org.zenframework.security.*;
import org.zenframework.security.annotation.AuthenticationRequest;
import org.zenframework.web.util.SessionUtils;
import org.zenframework.web.util.WebUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Session based implementation
 * Created by Zeal on 2019/4/22 0022.
 */
public class WebAccessControl<B extends AuthInfo> extends AbstractAccessControl<WebAuthToken,B> {

    public static final String DEFAULT_AUTH_INFO_SESSION_KEY = "authInfo";

    public static final String DEFAULT_USER_NAME_PARAM = "userName";

    public static final String DEFAULT_PASSWORD_PARAM = "password";

    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    public static final String DEFAULT_REQUIRED_CAPTCHA_PARAM = "sessionCaptcha";

    public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";

    public static final String DEFAULT_REMEMBER_ME_COOKIE_NAME = "_rm";

    private String authInfoSessionKey = DEFAULT_AUTH_INFO_SESSION_KEY;

    private String userNameParam = DEFAULT_USER_NAME_PARAM;

    private String passwordParam = DEFAULT_PASSWORD_PARAM;

    //Request captcha
    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    private int captchaSize = 4;

    private CaptchaGenerator captchaGenerator = new DefaultCaptchaGenerator();

    //Required captcha in session
    private String requiredCaptchaParam = DEFAULT_REQUIRED_CAPTCHA_PARAM;

    private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;

    private String rememberMeCookieName = DEFAULT_REMEMBER_ME_COOKIE_NAME;

    public String getAuthInfoSessionKey() {
        return authInfoSessionKey;
    }

    public void setAuthInfoSessionKey(String authInfoSessionKey) {
        this.authInfoSessionKey = authInfoSessionKey;
    }

    public String getUserNameParam() {
        return userNameParam;
    }

    public void setUserNameParam(String userNameParam) {
        this.userNameParam = userNameParam;
    }

    public String getPasswordParam() {
        return passwordParam;
    }

    public void setPasswordParam(String passwordParam) {
        this.passwordParam = passwordParam;
    }

    public String getCaptchaParam() {
        return captchaParam;
    }

    public void setCaptchaParam(String captchaParam) {
        this.captchaParam = captchaParam;
    }

    public String getRememberMeCookieName() {
        return rememberMeCookieName;
    }

    public String getRememberMeParam() {
        return rememberMeParam;
    }

    public void setRememberMeParam(String rememberMeParam) {
        this.rememberMeParam = rememberMeParam;
    }

    public void setRememberMeCookieName(String rememberMeCookieName) {
        this.rememberMeCookieName = rememberMeCookieName;
    }

    public void setCaptchaSize(int captchaSize) {
        if (captchaSize > 0) {
            this.captchaSize = captchaSize;
        }
    }

    public void setRequiredCaptchaParam(String requiredCaptchaParam) {
        this.requiredCaptchaParam = requiredCaptchaParam;
    }

    @Override
    public Class getEnableAnnotationClass() {
        return EnableWebAccessControl.class;
    }

    @Override
    public B getAuthenticatedInfo(HttpServletRequest request) {
        return SessionUtils.getAttribute(request, this.authInfoSessionKey);
    }

    @Override
    public void setAuthenticatedInfo(HttpServletRequest request, B authInfo) {
        SessionUtils.setAttribute(request, this.authInfoSessionKey, authInfo);
    }

    /**
     * Create auth token
     *
     * @param request
     * @return null or Result with error code
     */
    @Override
    public Result<WebAuthToken> createAuthenticationToken(HttpServletRequest request, boolean authenticationRequest) {

        if (authenticationRequest) {
            String userName = request.getParameter(this.userNameParam);
            if (StringUtils.isEmpty(userName)) {
                return new Result<>(WebAuthError.INVALID_PRINCIPAL);
            }
            String password = request.getParameter(this.passwordParam);
            if (StringUtils.isEmpty(password)) {
                return new Result<>(WebAuthError.INVALID_CREDENTIAL);
            }
            String requireCaptcha = SessionUtils.getAttribute(request, this.requiredCaptchaParam);
            String captcha = null;
            if (requireCaptcha != null) {
                captcha = request.getParameter(this.captchaParam);
                if (!requireCaptcha.equalsIgnoreCase(captcha)) {
                    return new Result<>(WebAuthError.INVALID_REQUEST_CAPTCHA);
                }
            }
            String rememberMeString = request.getParameter(this.rememberMeParam);
            boolean rememberMe = StringUtils.isNotEmpty(rememberMeString) &&
                    ("true".equalsIgnoreCase(rememberMeString) || "yes".equalsIgnoreCase(rememberMeString) ||
                            "on".equalsIgnoreCase(rememberMeString) || "1".equalsIgnoreCase(rememberMeString));
            WebAuthToken authToken = new WebAuthToken();
            authToken.setUserName(userName);
            authToken.setPassword(password);
            authToken.setRememberMe(rememberMe);
            authToken.setRequestCaptcha(captcha);
            authToken.setRequiredCaptcha(requireCaptcha);
            return new Result<>(WebAuthError.NO_ERROR, authToken);
        }
        else {
            Cookie rememberMeCookie = WebUtils.getCookie(request, this.rememberMeCookieName);
            if (rememberMeCookie == null || StringUtils.isEmpty(rememberMeCookie.getValue())) {
                return null;
            }
            else {
                String[] tokenValues = this.parseRememberMeCookie(rememberMeCookie);
                if (tokenValues != null && tokenValues.length == 2) {
                    String series = tokenValues[0];
                    String token = tokenValues[1];
                    if (StringUtils.isEmpty(series) || StringUtils.isEmpty(token)) {
                        return null;
                    }
                    else {
                        WebAuthToken authToken = new WebAuthToken();
                        authToken.setRememberMeSeries(series);
                        authToken.setRememberMeToken(token);
                        authToken.setRememberMe(true);
                        return new Result<>(WebAuthError.NO_ERROR, authToken);
                    }
                }
                else {
                    return null;
                }
            }
        }
    }

    /**
     * @return first one is series, second one is token
     */
    protected String[] parseRememberMeCookie(Cookie rememberMeCookie) {
        if (StringUtils.isEmpty(rememberMeCookie.getValue())) {
            //cancelRememberMeCookie(request, response);
            return null;
        } else {
            String[] values = StringUtils.split(rememberMeCookie.getValue(), '#');
            if (values.length != 2) {
                //this.cancelRememberMeCookie(request, response);
                return null;
            } else {
                String series = values[0];
                String token = values[1];
                if (StringUtils.isEmpty(series) || StringUtils.isEmpty(token)) {
                    //this.cancelRememberMeCookie(request, response);
                    return null;
                } else {
                    return new String[]{series, token};
                }
            }
        }
    }


    /**
     * clear cookie
     *
     * @param response
     */
    protected void removeRememberMeCookieIfNecessary(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, this.rememberMeCookieName);
        if (cookie != null) {
            cookie.setValue(null);
            cookie.setMaxAge(0);
            cookie.setPath(request.getContextPath().length() <= 0 ? "/" : request.getContextPath());
            response.addCookie(cookie);
        }
    }

    protected void removeRequiredCaptchaIfNecessary(HttpServletRequest request) {
        if (StringUtils.isNotEmpty(this.requiredCaptchaParam)) {
            SessionUtils.removeAttribute(request, this.requiredCaptchaParam);
        }
    }

    protected void addRememberMeCookieIfNecessary(HttpServletRequest request, HttpServletResponse response,
        WebAuthToken authToken, RememberMeToken rememberMeToken) {

        if (authToken.isRememberMe() && rememberMeToken != null && this.rememberMeCookieName != null) {
            String cookieValue = rememberMeToken.getRememberMeSeries() + '#' + rememberMeToken.getRememberMeToken();
            Cookie cookie = new Cookie(this.rememberMeCookieName, cookieValue);
            //FIXME Cookie path
            //Two weeks
            cookie.setMaxAge(1209600);
            cookie.setHttpOnly(true);
            cookie.setPath(request.getContextPath().length() <= 0 ? "/" : request.getContextPath());
            response.addCookie(cookie);
        }
    }

    @Override
    public void onAuthenticateSuccess(HttpServletRequest request, HttpServletResponse response,
        AuthenticationRequest authenticationRequest, AuthResult<WebAuthToken,B> authResult)  {

        removeRequiredCaptchaIfNecessary(request);
        addRememberMeCookieIfNecessary(request, response, authResult.getAuthenticationToken(), authResult.getRememberMeToken());
    }

    protected void generateSessionCaptchaIfNecessary(HttpServletRequest request, HttpServletResponse response, int errorCode) {
        if (errorCode == WebAuthError.INVALID_REQUEST_CAPTCHA || errorCode == WebAuthError.INVALID_REQUIRED_CAPTCHA ||
                errorCode == WebAuthError.INVALID_CREDENTIAL_CAPTCHA) {
            String captcha = this.captchaGenerator.generateCaptcha(this.captchaSize);
            SessionUtils.setAttribute(request, this.requiredCaptchaParam, captcha);
        }
        else if (errorCode != AuthError.NO_ERROR && SessionUtils.getAttribute(request, this.requiredCaptchaParam) != null) {
            String captcha = this.captchaGenerator.generateCaptcha(this.captchaSize);
            SessionUtils.setAttribute(request, this.requiredCaptchaParam, captcha);
        }
    }

    @Override
    public void onAuthenticateFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationRequest authenticationRequest, WebAuthToken authToken, int errorCode) throws Exception {

        this.removeRememberMeCookieIfNecessary(request, response);
        this.generateSessionCaptchaIfNecessary(request, response, errorCode);
    }

    @Override
    public void onRequireUserFailure(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        if (WebUtils.isAjaxRequest(request)) {
            response.setStatus(WebAuthError.INVALID_SESSION);
        }
        else {
             request.setAttribute(WebAuthError.ERROR_CODE, WebAuthError.INVALID_SESSION);
            throw new IllegalStateException("onRequireUserFailure, error code is " + WebAuthError.INVALID_SESSION);
        }
    }

    @Override
    public void onRequireRoleFailure(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, int errorCode) throws Exception {
        if (WebUtils.isAjaxRequest(request)) {
            response.setStatus(WebAuthError.ACCESS_DENIED);
        }
        else {
             request.setAttribute(WebAuthError.ERROR_CODE, WebAuthError.ACCESS_DENIED);
            throw new IllegalStateException("onRequireRoleFailure, error code is " + WebAuthError.ACCESS_DENIED);
        }
    }

    @Override
    public void onRequirePermissionFailure(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, int errorCode) throws Exception {
        if (WebUtils.isAjaxRequest(request)) {
            response.setStatus(WebAuthError.ACCESS_DENIED);
        }
        else {
             request.setAttribute(WebAuthError.ERROR_CODE, WebAuthError.ACCESS_DENIED);
            throw new IllegalStateException("onRequirePermissionFailure, error code is " + WebAuthError.ACCESS_DENIED);
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        this.removeRememberMeCookieIfNecessary(request, response);
        //SessionUtils.removeAttribute(request, this.authInfoSessionKey);
        SessionUtils.invalidate(request);
    }
}
