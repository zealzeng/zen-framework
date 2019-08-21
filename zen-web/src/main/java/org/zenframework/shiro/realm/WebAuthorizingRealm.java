package org.zenframework.shiro.realm;

import org.zenframework.util.StringUtils;
import org.zenframework.vo.Result;
import org.zenframework.shiro.error.AuthenticationError;
import org.zenframework.shiro.vo.AuthInfo;
import org.zenframework.shiro.vo.RememberMeToken;
import org.zenframework.shiro.vo.WebAuthToken;
import org.zenframework.web.vo.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Generic web authz realm using username,password and captcha
 * Created by Zeal on 2019/1/13 0013.
 */
public class WebAuthorizingRealm<T extends AuthInfo> extends AuthorizingRealm {

    private static final Logger logger = LogManager.getLogger(WebAuthorizingRealm.class);

    private AuthService<WebAuthToken,T> authService = null;

    public AuthService<WebAuthToken,T> getAuthService() {
        return authService;
    }

    public void setAuthService(AuthService<WebAuthToken,T> authService) {
        this.authService = authService;
    }

    public WebAuthorizingRealm() {
        this.setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

    @Override
    protected void onInit() {
        super.onInit();
        this.setAuthenticationTokenClass(WebAuthToken.class);
        if (StringUtils.isEmpty(this.getName())) {
            this.setName(this.getClass().getName());
        }
    }

    /**
     * Get roles and permissions
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        T authInfo = (T) getAvailablePrincipal(principals);
        //authInfo = this.authService.getAuthorizationInfo(authInfo);
        SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
        if (authInfo.getRoles() != null) {
            authzInfo.setRoles(authInfo.getRoles());
        }
        if (authInfo.getPermissions() != null) {
            authzInfo.setStringPermissions(authInfo.getPermissions());
        }
        return authzInfo;
    }

    /**
     * Get account
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        WebAuthToken requestToken = (WebAuthToken) token;
        if (!requestToken.validate()) {
            throw new AuthenticationError(AuthenticationError.INVALID_AUTHC_TOKEN);
        }
        T authInfo = null;
        try {
            Result<T> result = this.authService.authenticate(requestToken);
            if (result.getResultCode() != ErrorCode.NO_ERROR) {
                throw new AuthenticationError(result.getResultCode());
            }
            authInfo = result.getResultEntity();
        }
        catch (Exception e) {
            throw new AuthenticationError(AuthenticationError.GET_AUTHC_INFO_FAIL);
        }

        authInfo.clearSensitiveInfo();
        RememberMeToken rememberMeToken = authInfo.getRememberMeToken();
        if (rememberMeToken != null) {
            rememberMeToken.clearSensitiveInfo();
        }
        authInfo = this.authService.authenticateSuccess(requestToken, authInfo);
        //Ignore credential right now
        return new SimpleAuthenticationInfo(authInfo, null, this.getName());
    }

}
