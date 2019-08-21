package org.zenframework.shiro.realm;

import org.zenframework.vo.Result;
import org.zenframework.shiro.vo.AuthInfo;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Authentication and authorization service
 * Created by Zeal on 2019/1/13 0013.
 */
public interface AuthService<A extends AuthenticationToken,B extends AuthInfo> {

    /**
     * Authenticate and authority
     * @param token
     * @return
     */
     Result<B> authenticate(A token);

    /**
     * Auth success callback
     * @param token
     * @param authInfo
     */
    B authenticateSuccess(A token, B authInfo);

    /**
     * Auth failure callback
     * @param token
     * @param authInfo It won't be null
     * @return Must refresh the auth info
     */
    B authenticateFail(int errorCode, A token, B authInfo);

}
