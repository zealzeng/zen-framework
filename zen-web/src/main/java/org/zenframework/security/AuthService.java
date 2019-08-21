package org.zenframework.security;

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
     AuthResult<A,B> authenticate(A token);


}
