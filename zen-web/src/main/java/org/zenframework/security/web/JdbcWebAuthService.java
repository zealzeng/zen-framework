package org.zenframework.security.web;

import org.zenframework.util.CryptoUtils;
import org.zenframework.util.DateUtils;
import org.zenframework.util.StringUtils;
import org.zenframework.security.AuthInfo;
import org.zenframework.security.AuthResult;
import org.zenframework.security.RememberMeToken;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zenframework.util.UUIDUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of web auth service
 * Created by Zeal on 2019/4/22 0022.
 */
public class JdbcWebAuthService<A extends WebAuthToken,B extends AuthInfo> extends WebAuthService<A,B> {

    public static final String DEF_AUTHENTICATE_QUERY = "select * from uc_user where user_name=?";

    public static final String DEF_PERMISSION_QUERY = "select * from uc_user_auth where user_id=? and deleted=0";

    public static final String DEF_ROLE_QUERY = "select * from uc_user_role where user_id=? and deleted=0";

    //=====================================================================================================================

    public static final String DEF_REMEMBER_ME_BY_SERIES_QUERY = "select * from uc_login_token where series=? and deleted=0";

    public static final String DEF_ADD_REMEMBER_ME_SQL = "insert into uc_login_token(user_id,series,token,last_time,deleted)VALUES(?,?,?,?,?)";

    public static final String DEF_DELETE_REMEMBER_ME_SQL = "update uc_login_token set deleted=1 where series=?";

    public static final String DEF_UPDATE_REMEMBER_ME_SQL = "update uc_login_token set token=?,last_time=? where series=?";

    public static final String DEF_AUTH_INFO_BY_REMEMBER_ME_QUERY = "select * from uc_user where user_id=?";

    public static final int DEF_REMEMBER_ME_VALIDITY_DAYS = 14;

    private JdbcTemplate jdbcTemplate = null;

    protected BeanPropertyRowMapper<B> authInfoMapper = null;

    //Query user, must return AuthInfo class
    private String authenticateQuery = DEF_AUTHENTICATE_QUERY;

    //Query user role, must return role name
    private String roleQuery = DEF_ROLE_QUERY;

    //Query user permission, must return permission name
    private String permissionQuery = DEF_PERMISSION_QUERY;

    //Since remember me is optional

    protected BeanPropertyRowMapper<? extends RememberMeToken> rememberMeTokenMapper = null;

    //Query remember me token by series
    private String rememberMeQuery = DEF_REMEMBER_ME_BY_SERIES_QUERY;

    //Delete remember me token by series
    private String deleteRememberMeSql = DEF_DELETE_REMEMBER_ME_SQL;

    //Refresh remember me token, update token,last_time from xx where series=
    private String updateRememberMeSql = DEF_UPDATE_REMEMBER_ME_SQL;

    //Query auth info by remember me
    private String rememberMeAuthInfoQuery = DEF_AUTH_INFO_BY_REMEMBER_ME_QUERY;

    //Add remember me sql
    private String addRememberMeSql = DEF_ADD_REMEMBER_ME_SQL;

    private int rememberMeValidityDays = DEF_REMEMBER_ME_VALIDITY_DAYS;

    public JdbcWebAuthService() {
        this(null);
    }

    public JdbcWebAuthService(Class<? extends RememberMeToken> rememberMeTokenClass) {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        Type[] types = pt.getActualTypeArguments();
        Class<B> bType = (Class<B>) pt.getActualTypeArguments()[1];
        authInfoMapper = new BeanPropertyRowMapper(bType);
        if (rememberMeTokenClass != null) {
            setRememberMeTokenClass(rememberMeTokenClass);
        }
    }

    public <T extends RememberMeToken> void setRememberMeTokenClass(Class<T> clazz) {
        rememberMeTokenMapper = new BeanPropertyRowMapper(clazz);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setAuthenticateQuery(String authenticateQuery) {
        this.authenticateQuery = authenticateQuery;
    }

    public void setRoleQuery(String roleQuery) {
        this.roleQuery = roleQuery;
    }

    public void setPermissionQuery(String permissionQuery) {
        this.permissionQuery = permissionQuery;
    }

    public String getAuthenticateQuery() {
        return authenticateQuery;
    }

    public String getRoleQuery() {
        return roleQuery;
    }

    public String getPermissionQuery() {
        return permissionQuery;
    }

    public String getRememberMeQuery() {
        return rememberMeQuery;
    }

    public void setRememberMeQuery(String rememberMeQuery) {
        this.rememberMeQuery = rememberMeQuery;
    }

    public String getDeleteRememberMeSql() {
        return deleteRememberMeSql;
    }

    public void setDeleteRememberMeSql(String deleteRememberMeSql) {
        this.deleteRememberMeSql = deleteRememberMeSql;
    }

    public String getUpdateRememberMeSql() {
        return updateRememberMeSql;
    }

    public void setUpdateRememberMeSql(String updateRememberMeSql) {
        this.updateRememberMeSql = updateRememberMeSql;
    }

    public String getRememberMeAuthInfoQuery() {
        return rememberMeAuthInfoQuery;
    }

    public void setRememberMeAuthInfoQuery(String rememberMeAuthInfoQuery) {
        this.rememberMeAuthInfoQuery = rememberMeAuthInfoQuery;
    }

    public String getAddRememberMeSql() {
        return addRememberMeSql;
    }

    public void setAddRememberMeSql(String addRememberMeSql) {
        this.addRememberMeSql = addRememberMeSql;
    }

    public int getRememberMeValidityDays() {
        return rememberMeValidityDays;
    }

    public void setRememberMeValidityDays(int rememberMeValidityDays) {
        this.rememberMeValidityDays = rememberMeValidityDays;
    }

    /**
     * Authenticate and authorize by login or auto-login
     * @param token
     * @return
     */
    @Override
    public AuthResult<A,B> authenticate(A token) {
        if (token.isUsernamePasswordAuthentication()) {
            return this.authenticateByUser(token);
        }
        else if (this.rememberMeTokenMapper != null && token.isRememberMeAuthentication()) {
            return this.authenticateByRememberMe(token);
        }
        else {
            return new AuthResult<>(WebAuthError.OTHER_AUTHC_ERROR);
        }
    }

    /**
     * Get auth info by remember me cookie
     * @param webAuthToken
     * @return
     */
    protected AuthResult<A,B> authenticateByRememberMe(A webAuthToken) {

        String series = webAuthToken.getRememberMeSeries();
        String token = webAuthToken.getRememberMeToken();
        RememberMeToken rememberMeToken = queryRememberMeTokenBySeries(series);
        if (rememberMeToken == null) {
            this.authenticateFailure(WebAuthError.REMEMBER_ME_TOKEN_NOT_EXIST, webAuthToken, null);
            return new AuthResult<>(WebAuthError.REMEMBER_ME_TOKEN_NOT_EXIST);
        }
        //Same series and different token, cookie might be stolen
        if (!token.equals(rememberMeToken.getRememberMeToken()) || isRememberMeTokenExpired(rememberMeToken)) {
            //Delete remember me token by series
            this.deleteRememberMeBySeries(rememberMeToken.getRememberMeSeries());
            this.authenticateFailure(WebAuthError.REMEMBER_ME_TOKEN_EXPIRED, webAuthToken, null);
            return new AuthResult<>(WebAuthError.REMEMBER_ME_TOKEN_EXPIRED);
        }
        this.updateRememberMeTokenBySeries(rememberMeToken);
        B authInfo = this.queryRememberMeAuthInfo(rememberMeToken);
        if (authInfo !=  null) {
            this.getAuthorizationInfo(authInfo);
            //authInfo.setRememberMeToken(rememberMeToken);
            AuthResult<A,B> result = new AuthResult(WebAuthError.NO_ERROR);
            result.setAuthInfo(authInfo);
            result.setAuthenticationToken(webAuthToken);
            result.setRememberMeToken(rememberMeToken);
            this.authenticateSuccess(webAuthToken, authInfo, rememberMeToken);
            return result;
        }
        else {
            this.authenticateFailure(WebAuthError.GET_AUTHC_INFO_FAIL, webAuthToken, null);
            return new AuthResult<>(WebAuthError.GET_AUTHC_INFO_FAIL);
        }
    }

    protected B queryRememberMeAuthInfo(RememberMeToken rememberMeToken) {
        if (StringUtils.isEmpty(this.rememberMeAuthInfoQuery) || this.authInfoMapper == null) {
            return null;
        }
        return this.queryForObject(this.rememberMeAuthInfoQuery, this.authInfoMapper, (Integer) rememberMeToken.getAuthInfoKey());
    }

    private boolean isRememberMeTokenExpired(RememberMeToken token) {
        Date lastTime = token.getLastTime();
        Date expiredTime = DateUtils.addDays(lastTime, this.getRememberMeValidityDays());
        Date now = new Date();
        return now.compareTo(expiredTime) >= 0;
    }

    /**
     * Authenticate by username and password
     * @param token
     * @return
     */
    protected AuthResult<A,B> authenticateByUser(A token) {
        if (StringUtils.isEmpty(this.authenticateQuery)) {
            throw new IllegalStateException("authenticateQuery is required");
        }
        B authInfo = queryForObject(authenticateQuery, authInfoMapper, token.getUserName());
        if (authInfo == null) {
            this.authenticateFailure(WebAuthError.ACCOUNT_NOT_EXIST, token, null);
            return new AuthResult<>(WebAuthError.ACCOUNT_NOT_EXIST);
        }
        int errorCode = this.validateAuthInfo(authInfo);
        if (errorCode != WebAuthError.NO_ERROR) {
            this.authenticateFailure(errorCode, token, null);
            return new AuthResult<>(errorCode);
        }
        errorCode = this.validateCaptcha(token, authInfo);
        if (errorCode != WebAuthError.NO_ERROR) {
            this.authenticateFailure(errorCode, token, authInfo);
            return new AuthResult<>(errorCode);
        }
        boolean matched = this.matchCredentials(token, authInfo);
        if (!matched) {
             this.authenticateFailure(WebAuthError.INVALID_CREDENTIAL, token, authInfo);
             if (authInfo.isCaptchaRequired()) {
                return new AuthResult<>(WebAuthError.INVALID_CREDENTIAL_CAPTCHA);
            }
            else {
                return new AuthResult<>(WebAuthError.INVALID_CREDENTIAL);
            }
        }
        //Get roles and permissions
        this.getAuthorizationInfo(authInfo);
        AuthResult result = new AuthResult(WebAuthError.NO_ERROR);
        if (this.rememberMeTokenMapper != null) {
            //Remember me
            if (token.isRememberMe()) {
                //Update if series's available
                if (StringUtils.isNotEmpty(token.getRememberMeSeries())) {
                    RememberMeToken rememberMeToken = queryRememberMeTokenBySeries(token.getRememberMeSeries());
                    if (rememberMeToken != null && rememberMeToken.getAuthInfoKey().equals(authInfo.getAuthInfoKey())) {
                        updateRememberMeTokenBySeries(rememberMeToken);
                        //authInfo.setRememberMeToken(rememberMeToken);
                        result.setRememberMeToken(rememberMeToken);
                    } else {
                        rememberMeToken = this.addRememberMeToken(token, authInfo);
                        //authInfo.setRememberMeToken(rememberMeToken);
                        result.setRememberMeToken(rememberMeToken);
                    }
                }
                //Add new one if series is not available
                else {
                    RememberMeToken rememberMeToken = this.addRememberMeToken(token, authInfo);
                    //authInfo.setRememberMeToken(rememberMeToken);
                    result.setRememberMeToken(rememberMeToken);
                }
            } else {
                if (StringUtils.isNotEmpty(token.getRememberMeSeries())) {
                    RememberMeToken rememberMeToken = queryRememberMeTokenBySeries(token.getRememberMeSeries());
                    if (rememberMeToken != null && rememberMeToken.getAuthInfoKey().equals(authInfo.getAuthInfoKey())) {
                        this.deleteRememberMeBySeries(token.getRememberMeSeries());
                    }
                }
            }
        }
        //Success callback
        this.authenticateSuccess(token, authInfo, result.getRememberMeToken());
        result.setAuthInfo(authInfo);
        result.setAuthenticationToken(token);
        return result;
    }

    /**
     * Validate auth info
     * @param authInfo
     * @return
     */
    protected int validateAuthInfo(B authInfo) {
        if (authInfo.isPrincipalsLocked()) {
            return WebAuthError.ACCOUNT_LOCKED;
        }
        else if (authInfo.isPrincipalsExpired()) {
            return WebAuthError.ACCOUNT_EXPIRED;
        }
        else if (authInfo.isCredentialsExpired()) {
            return WebAuthError.CREDENTIAL_EXPIRED;
        }
        else {
            return WebAuthError.NO_ERROR;
        }
    }

    /**
     * Authentication failure
     * @param errorCode
     * @param token
     * @param authInfo It can be null
     * @return
     */
    protected void authenticateFailure(int errorCode, WebAuthToken token, B authInfo) {
    }

    /**
     * Validate captcha
     * @param token
     * @param authInfo
     * @return
     */
    protected int validateCaptcha(WebAuthToken token, B authInfo) {
        //Need to check captcha
        if (authInfo.isCaptchaRequired()) {
            //Session has no captcha, maybe the session is timeout
            if (StringUtils.isEmpty(token.getRequiredCaptcha())) {
                return WebAuthError.INVALID_REQUIRED_CAPTCHA;
            }
            else if (!StringUtils.equalsIgnoreCase(token.getRequiredCaptcha(), token.getRequestCaptcha())) {
                //this.authenticateFailure(WebAuthError.INVALID_REQUEST_CAPTCHA, token, authInfo);
                return WebAuthError.INVALID_REQUEST_CAPTCHA;
            }
        }
        return WebAuthError.NO_ERROR;
    }

    /**
     * Check whether the passwords are matched
     * @param token
     * @param authInfo
     * @return
     */
    protected boolean matchCredentials(WebAuthToken token, B authInfo) {
        String credentials = authInfo.getCredentials();
        if (credentials == null) {
            return false;
        }
        String password = credentials;
        String rawPassword = token.getPassword();
        return CryptoUtils.pwdMatches(rawPassword, password);
    }

    /**
     * Get user role and permission
     * @param authInfo
     * @return
     */
    protected void getAuthorizationInfo(B authInfo) {
        if (StringUtils.isNotEmpty(this.roleQuery)) {
            List<String> roles = this.jdbcTemplate.queryForList(this.roleQuery, String.class, authInfo.getAuthInfoKey());
            authInfo.setRoles(roles);
        }
        if (StringUtils.isNotEmpty(this.permissionQuery)) {
            List<String> permissions = this.jdbcTemplate.queryForList(this.permissionQuery, String.class, authInfo.getAuthInfoKey());
            authInfo.setPermissions(permissions);
        }
    }

    /**
     * Query remember me token by series
     * @param series
     * @return
     */
    protected RememberMeToken queryRememberMeTokenBySeries(String series) {
        if (StringUtils.isEmpty(this.rememberMeQuery) || this.rememberMeTokenMapper == null) {
            return null;
        }
        return this.queryForObject(this.rememberMeQuery, this.rememberMeTokenMapper, series);
    }

    /**
     * Update new token by series
     * @param rememberMeToken
     * @return
     */
    protected int updateRememberMeTokenBySeries(RememberMeToken rememberMeToken) {
        if (StringUtils.isNotEmpty(this.updateRememberMeSql)) {
            String newToken = UUIDUtils.defaultUUID();
            java.util.Date now = new java.util.Date();
            rememberMeToken.setRememberMeToken(newToken);
            rememberMeToken.setLastTime(now);
            return this.jdbcTemplate.update(this.updateRememberMeSql, newToken, now, rememberMeToken.getRememberMeSeries());
        }
        else {
            return 0;
        }
    }

    /**
     * Add remember me token
     * @param authInfo
     * @return
     */
    protected RememberMeToken addRememberMeToken(A authToken, B authInfo) {
        if (StringUtils.isEmpty(this.addRememberMeSql) || this.rememberMeTokenMapper == null) {
            return null;
        }
        final String newToken = UUIDUtils.defaultUUID();
        final String newSeries = UUIDUtils.defaultUUID();
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        this.jdbcTemplate.update(addRememberMeSql, authInfo.getAuthInfoKey(), newSeries, newToken, now, 0);
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        this.jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(addRememberMeSql, Statement.RETURN_GENERATED_KEYS);
//                ps.setInt(1, (Integer)authInfo.getAuthInfoKey());
//                ps.setString(2, newSeries);
//                ps.setString(3, newToken);
//                ps.setTimestamp(4, now);
//                ps.setInt(5, 0);
//                return ps;
//            }
//        }, keyHolder);

        RememberMeToken loginToken = null;
        try {
            loginToken = this.rememberMeTokenMapper.getMappedClass().newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //DefaultWebRememberMeToken loginToken = new DefaultWebRememberMeToken();
        //loginToken.setDeleted(DefaultWebRememberMeToken.DELETED_NO);
        loginToken.setLastTime(now);
        //loginToken.setAutoIncreaseTokenId(keyHolder.getKey().intValue());
        loginToken.setRememberMeSeries(newSeries);
        loginToken.setRememberMeToken(newToken);
        loginToken.setAuthInfoKey(authInfo.getAuthInfoKey());
        return loginToken;
    }


    protected int deleteRememberMeBySeries(String series) {
        if (StringUtils.isNotEmpty(this.deleteRememberMeSql)) {
            return this.jdbcTemplate.update(this.deleteRememberMeSql, series);
        }
        else {
            return 0;
        }
    }

    /**
     * Sucess callback
     * @param token
     * @param authInfo
     * @param rememberMeToken
     */
    protected void authenticateSuccess(A token, B authInfo, RememberMeToken rememberMeToken) {
    }

    /**
     * Query for single object
     * @param sql
     * @param mapper
     * @param params
     * @return
     */
    protected  <Z>Z queryForObject(String sql, BeanPropertyRowMapper<Z> mapper, Object... params) {
        List<Z> list = this.jdbcTemplate.query(sql, mapper, params);
        if (list == null || list.size() <= 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }
}
