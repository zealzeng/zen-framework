package org.zenframework.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import org.zenframework.captcha.CaptchaGenerator;
import org.zenframework.captcha.DefaultCaptchaGenerator;
import org.zenframework.security.AuthError;
import org.zenframework.shiro.error.AuthenticationError;
import org.zenframework.shiro.vo.AuthInfo;
import org.zenframework.shiro.vo.RememberMeToken;
import org.zenframework.shiro.vo.WebAuthToken;
import org.zenframework.web.util.SessionUtils;
import org.zenframework.web.util.WebUtils;
import org.zenframework.web.error.WebError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.context.MessageSource;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Using UserPwdCaptchaToken
 * Created by Zeal on 2019/1/16 0016.
 */
public class WebAuthenticationFilter extends FormAuthenticationFilter {

    public static final String FILTER_NAME = "webAuthc";

    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    public static final int DEFAULT_CAPTCHA_LENGTH = 4;

    private static final Logger log = LogManager.getLogger(WebAuthenticationFilter.class);

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    private String sessionCaptchaParam = DEFAULT_CAPTCHA_PARAM;

    private MessageSource messageSource = null;

    private int captchaLength = DEFAULT_CAPTCHA_LENGTH;

    private CaptchaGenerator captchaGenerator = new DefaultCaptchaGenerator();

    public String getCaptchaParam() {
        return captchaParam;
    }

    public String getCaptchaParam(ServletRequest request) {
        return StringUtils.clean(request.getParameter(captchaParam));
    }

    public void setCaptchaParam(String captchaParam) {
        this.captchaParam = captchaParam;
    }

    public String getSessionCaptchaParam() {
        return sessionCaptchaParam;
    }

    public void setSessionCaptchaParam(String sessionCaptchaParam) {
        this.sessionCaptchaParam = sessionCaptchaParam;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public CaptchaGenerator getCaptchaGenerator() {
        return captchaGenerator;
    }

    public int getCaptchaLength() {
        return captchaLength;
    }

    public void setCaptchaLength(int captchaLength) {
        this.captchaLength = captchaLength;
    }

    public void setCaptchaGenerator(CaptchaGenerator captchaGenerator) {
        this.captchaGenerator = captchaGenerator;
    }

    /**
     * Create login token
     * @param request
     * @param response
     * @return
     */
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        HttpServletRequest req = (HttpServletRequest) request;
        String requestCaptcha = null;
        String sessionCaptcha = null;
        if (StringUtils.hasLength(this.captchaParam)) {
            requestCaptcha = getCaptchaParam(request);
        }
        if (StringUtils.hasLength(this.sessionCaptchaParam)) {
            sessionCaptcha = SessionUtils.getAttribute(req, sessionCaptchaParam);
        }
        WebAuthToken token = new WebAuthToken();
        token.setUsername(username);
        if (password != null) {
            token.setPassword(password.toCharArray());
        }
        token.setRememberMe(rememberMe);
        token.setHost(host);
        token.setRequestCaptcha(requestCaptcha);
        token.setSessionCaptcha(sessionCaptcha);

        return token;
    }

    protected AuthenticationToken createRememberMeToken(HttpServletRequest req, HttpServletResponse resp, Subject subject) {
        Object principal = subject.getPrincipal();
        if (principal == null || !(principal instanceof AuthInfo)) {
            return null;
        }
        AuthInfo authInfo = (AuthInfo) principal;
        RememberMeToken rememberMeToken = authInfo.getRememberMeToken();
        if (rememberMeToken == null || !StringUtils.hasLength(rememberMeToken.getToken()) ||
            !StringUtils.hasLength(rememberMeToken.getSeries())) {
            return null;
        }
        WebAuthToken token = new WebAuthToken();
        String host = getHost(req);
        token.setRememberMe(true);
        token.setHost(host);
        token.setRememberMeToken(rememberMeToken.getToken());
        token.setRememberMeSeries(rememberMeToken.getSeries());
        return token;
    }

    protected String getErrorMessage(int errorCode) {
        String errorMessage = null;
        //FIXME Handle local and error attribute key
        if (this.messageSource != null) {
            try {
                errorMessage = this.messageSource.getMessage(WebError.KEY_PREFIX + errorCode, null, null);
            }
            catch (Exception ex) {
                log.warn("Failed to get error code " + errorCode + " from message source");
            }
        }
        if (errorMessage == null) {
            errorMessage = "Error code is " + errorCode;
        }
        return errorMessage;
    }

    protected boolean isRefreshCaptchaError(int errorCode) {
        return errorCode == AuthenticationError.INVALID_REQUEST_CAPTCHA ||
            errorCode == AuthenticationError.INVALID_SESSION_CAPTCHA ||
            errorCode == AuthenticationError.INVALID_CREDENTIAL_CAPTCHA;
    }

    protected void refreshCaptcha(HttpServletRequest req) {
        String captcha = captchaGenerator.generateCaptcha(this.captchaLength);
        SessionUtils.setAttribute(req, this.sessionCaptchaParam, captcha);
    }

    protected boolean onAjaxLoginFailure(HttpServletRequest req, HttpServletResponse resp, int errorCode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", errorCode);
        jsonObject.put("resultMessage", this.getErrorMessage(errorCode));
        try {
            resp.getWriter().write(jsonObject.toJSONString());
        }
        catch (Exception ex) {
            log.warn("Failed to write json string " + jsonObject.toJSONString());
        }
        return false;
    }

    protected boolean onWebLoginFailure(HttpServletRequest req, HttpServletResponse resp, int errorCode) {
        req.setAttribute("resultCode", errorCode);
        req.setAttribute("resultMessage", this.getErrorMessage(errorCode));
        //setFailureAttribute(request, e);
        //login failed, let request continue back to the login page:
        return true;
    }

    /**
     * Login failure handler
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        if (log.isDebugEnabled()) {
            log.debug( "Authentication exception", e );
        }
        int errorCode = 0;
        if (e instanceof AuthenticationError) {
            AuthenticationError authError = (AuthenticationError) e;
            errorCode = authError.getErrorCode();
        }
        //FIXME Classify and translate exception
        else {
            errorCode = AuthenticationError.OTHER_AUTHC_ERROR;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (this.isRefreshCaptchaError(errorCode)) {
            this.refreshCaptcha(req);
        }
        if (WebUtils.isAjaxRequest(req)) {
            return onAjaxLoginFailure(req, resp, errorCode);
        }
        else {
            return onWebLoginFailure(req, resp, errorCode);
        }
    }

    /**
     * @param request
     * @param response
     * @return false access's denied, true - continue request
     * @throws Exception
     */
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        }

        Subject subject = this.getSubject(request, response);
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //FIXME
        if (subject.isRemembered()) {
            AuthenticationToken token = createRememberMeToken(req, resp, subject);
            try {
                subject.login(token);
                return onLoginSuccess(token, subject, request, response);
              }
              catch (AuthenticationException e) {
                return onLoginFailure(token, e, request, response);
            }
        }

        if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
        }
        if (WebUtils.isAjaxRequest(req)) {
            resp.setStatus(AuthError.ACCESS_DENIED);
        }
        else {
            saveRequestAndRedirectToLogin(request, response);
        }
        return false;
    }

}
