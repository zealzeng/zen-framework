package org.zenframework.shiro.realm;

import org.zenframework.util.CryptoUtils;
import org.zenframework.util.DateUtils;
import org.zenframework.util.StringUtils;
import org.zenframework.common.Result;
import org.zenframework.shiro.error.AuthenticationError;
import org.zenframework.shiro.vo.AuthInfo;
import org.zenframework.shiro.vo.RememberMeToken;
import org.zenframework.shiro.vo.WebAuthToken;
import org.zenframework.web.error.WebError;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Created by Zeal on 2019/1/14 0014.
 */
public abstract class WebAuthService<B extends AuthInfo> implements AuthService<WebAuthToken,B> {

    public static final int DEFAULT_REMEMBER_ME_VALIDITY_DAYS = 14;

    protected BeanPropertyRowMapper<B> authInfoMapper = null;

    private JdbcTemplate jdbcTemplate = null;

    //Query user, must return AuthInfo class
    private String authenticateQuery = null;

    //Query user role, must return role name
    private String userRoleQuery = null;

    //Query user permission, must return permission name
    private String permissionQuery = null;

    private int rememberMeValidityDays = DEFAULT_REMEMBER_ME_VALIDITY_DAYS;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRememberMeValidityDays() {
        return rememberMeValidityDays;
    }

    public void setRememberMeValidityDays(int rememberMeValidityDays) {
        this.rememberMeValidityDays = rememberMeValidityDays;
    }

    public WebAuthService() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        Class<B> type = (Class<B>) pt.getActualTypeArguments()[0];
        authInfoMapper = new BeanPropertyRowMapper(type);
    }

    /**
     * Authenticate and authority
     * @param token
     * @return
     */
    public Result<B> authenticate(WebAuthToken token) {

        boolean formLogin = StringUtils.isNotEmpty(token.getUsername());
        if (formLogin) {
            return this.authenticateByUser(token);
        }
        boolean rememberMeLogin = StringUtils.isNotEmpty(token.getRememberMeToken()) && StringUtils.isNotEmpty(token.getRememberMeSeries());
        if (rememberMeLogin) {
            return this.authenticateByRememberMe(token);
        }
        else {
            return new Result<>(AuthenticationError.OTHER_AUTHC_ERROR);
        }
    }

    /**
     * Query for single object
     * @param sql
     * @param mapper
     * @param params
     * @param <C>
     * @return
     */
    protected  <C>C queryForObject(String sql, BeanPropertyRowMapper<C> mapper, Object... params) {
        List<C> list = this.jdbcTemplate.query(sql, mapper, params);
        if (list == null || list.size() <= 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    /**
     * Validate auth info
     * @param authInfo
     * @return
     */
    protected int validateAuthInfo(B authInfo) {
        if (authInfo.isPrincipalsLocked()) {
            return AuthenticationError.ACCOUNT_LOCKED;
        }
        else if (authInfo.isPrincipalsExpired()) {
            return AuthenticationError.ACCOUNT_EXPIRED;
        }
        else if (authInfo.isCredentialsExpired()) {
            return AuthenticationError.CREDENTIAL_EXPIRED;
        }
        else {
            return WebError.NO_ERROR;
        }
    }

    protected int validateCaptcha(WebAuthToken token, B authInfo) {
        //Need to check the captcha
        if (authInfo.isAuthCaptchaRequired()) {
            //Session has no captcha, maybe the session is timeout, not trigger the failure handler
            if (StringUtils.isEmpty(token.getSessionCaptcha())) {
                return AuthenticationError.INVALID_SESSION_CAPTCHA;
            }
            else if (!StringUtils.equalsIgnoreCase(token.getSessionCaptcha(), token.getRequestCaptcha())) {
                this.authenticateFail(AuthenticationError.INVALID_REQUEST_CAPTCHA, token, authInfo);
                return AuthenticationError.INVALID_REQUEST_CAPTCHA;
            }
        }
        return WebError.NO_ERROR;
    }

    /**
     * Authenticate by username and password
     * @param token
     * @return
     */
    protected Result<B> authenticateByUser(WebAuthToken token) {
        if (StringUtils.isEmpty(this.authenticateQuery)) {
            return new Result<>(AuthenticationError.AUTHC_CONFIG_ERROR);
        }
        B authInfo = queryForObject(authenticateQuery, authInfoMapper, token.getUsername());
        if (authInfo == null) {
            return new Result<>(AuthenticationError.ACCOUNT_NOT_EXIST);
        }
        int errorCode = this.validateAuthInfo(authInfo);
        if (errorCode != WebError.NO_ERROR) {
            this.authenticateFail(errorCode, token, authInfo);
            return new Result<>(errorCode);
        }
        errorCode = this.validateCaptcha(token, authInfo);
        if (errorCode != WebError.NO_ERROR) {
            this.authenticateFail(errorCode, token, authInfo);
            return new Result<>(errorCode);
        }
        boolean matched = this.matchCredentials(token, authInfo);
        if (!matched) {
             authInfo = this.authenticateFail(AuthenticationError.INVALID_CREDENTIAL, token, authInfo);
             if (authInfo.isAuthCaptchaRequired()) {
                return new Result<>(AuthenticationError.INVALID_CREDENTIAL_CAPTCHA);
            }
            else {
                return new Result<>(AuthenticationError.INVALID_CREDENTIAL);
            }
        }
        //Get roles and permissions
        authInfo = this.getAuthorizationInfo(authInfo);
        //Remember me
        if (token.isRememberMe()) {
            //Update if series's available
            if (StringUtils.isNotEmpty(token.getRememberMeSeries())) {
                RememberMeToken rememberMeToken = queryRememberMeTokenBySeries(token.getRememberMeSeries());
                if (rememberMeToken != null && rememberMeToken.getAuthInfoKey().equals(authInfo.getAuthInfoKey())) {
                    updateRememberMeTokenBySeries(rememberMeToken);
                    authInfo.setRememberMeToken(rememberMeToken);
                }
            }
            //Add new one if series is not available
            else {
                RememberMeToken rememberMeToken = this.addRememberMeToken(token, authInfo);
                authInfo.setRememberMeToken(rememberMeToken);
            }
        }
        else {
            if (StringUtils.isNotEmpty(token.getRememberMeSeries())) {
                RememberMeToken rememberMeToken = queryRememberMeTokenBySeries(token.getRememberMeSeries());
                if (rememberMeToken != null && rememberMeToken.getAuthInfoKey().equals(authInfo.getAuthInfoKey())) {
                    this.deleteRememberMeBySeries(token.getRememberMeSeries());
                }
            }
        }
        //Success callback
        authInfo = this.authenticateSuccess(token, authInfo);
        return new Result<>(WebError.NO_ERROR, authInfo);
    }

    /**
     * Add new remember me token
     * @param token
     * @param authInfo
     * @return
     */
    protected abstract RememberMeToken addRememberMeToken(WebAuthToken token, B authInfo);

    /**
     * Get remember me token by series
     * @param series
     * @return
     */
    protected abstract RememberMeToken queryRememberMeTokenBySeries(String series);

    /**
     * Update token,last_time by series
     * @param rememberMeToken
     */
    protected abstract int updateRememberMeTokenBySeries(RememberMeToken rememberMeToken);

    /**
     * Delete remember me token by series
     * @param series
     * @return update count
     */
    protected abstract int deleteRememberMeBySeries(String series);


    /**
     * Query auth_info table by remember me token info
     * @param rememberMeToken
     * @return
     */
    protected abstract B queryRememberMeAuthInfo(RememberMeToken rememberMeToken);


    /**
     * Get auth info by remember me cookie
     * @param webAuthToken
     * @return
     */
    protected Result<B> authenticateByRememberMe(WebAuthToken webAuthToken) {

        String series = webAuthToken.getRememberMeSeries();
        String token = webAuthToken.getRememberMeToken();
        RememberMeToken rememberMeToken = queryRememberMeTokenBySeries(series);
        if (rememberMeToken == null) {
            return new Result<>(AuthenticationError.REMEMBER_ME_TOKEN_NOT_EXIST);
        }
        //Same series and different token, cookie might be stolen
        if (!token.equals(rememberMeToken) || isRememberMeTokenExpired(rememberMeToken)) {
            //Delete remember me token by series
            this.deleteRememberMeBySeries(rememberMeToken.getSeries());
            return new Result<>(AuthenticationError.REMEMBER_ME_TOKEN_EXPIRED);
        }
        this.updateRememberMeTokenBySeries(rememberMeToken);

        B authInfo = this.queryRememberMeAuthInfo(rememberMeToken);
        if (authInfo !=  null) {
            authInfo.setRememberMeToken(rememberMeToken);
            return new Result<>(WebError.NO_ERROR, authInfo);
        }
        else {
            return new Result<>(AuthenticationError.AUTHC_CONFIG_ERROR);
        }
    }

    private boolean isRememberMeTokenExpired(RememberMeToken token) {
        Date lastTime = token.getLastTime();
        Date expiredTime = DateUtils.addDays(lastTime, this.getRememberMeValidityDays());
        Date now = new Date();
        return now.compareTo(expiredTime) >= 0;
    }


    /**
     * Check whether the passwords are matched
     * @param token
     * @param authInfo
     * @return
     */
    protected boolean matchCredentials(WebAuthToken token, B authInfo) {
        Object credentials = authInfo.getCredentials();
        if (credentials == null || !(credentials instanceof String)) {
            return false;
        }
        String password = (String) credentials;
        String rawPassword = new String(token.getPassword());
        return CryptoUtils.pwdMatches(rawPassword, password);
    }

    @Override
    public B authenticateSuccess(WebAuthToken token, B authInfo) {
        return authInfo;
    }

    /**
     *
     * @param errorCode
     * @param token
     * @param authInfo It won't be null
     * @return
     */
    @Override
    public B authenticateFail(int errorCode, WebAuthToken token, B authInfo) {
        return authInfo;
    }

    /**
     * Get user role and permission
     * @param authInfo
     * @return
     */
    public B getAuthorizationInfo(B authInfo) {
        if (StringUtils.isNotEmpty(this.userRoleQuery)) {
            List<String> roles = this.jdbcTemplate.queryForList(this.userRoleQuery, String.class, authInfo.getAuthInfoKey());
            authInfo.setRoles(roles);
        }
        if (StringUtils.isNotEmpty(this.permissionQuery)) {
            List<String> permissions = this.jdbcTemplate.queryForList(this.permissionQuery, String.class, authInfo.getAuthInfoKey());
            authInfo.setPermissions(permissions);
        }
        return authInfo;
    }

    public String getAuthenticateQuery() {
        return authenticateQuery;
    }

    public void setAuthenticateQuery(String authenticateQuery) {
        this.authenticateQuery = authenticateQuery;
    }

    public String getUserRoleQuery() {
        return userRoleQuery;
    }

    public void setUserRoleQuery(String userRoleQuery) {
        this.userRoleQuery = userRoleQuery;
    }

    public String getPermissionQuery() {
        return permissionQuery;
    }

    public void setPermissionQuery(String permissionQuery) {
        this.permissionQuery = permissionQuery;
    }
}
