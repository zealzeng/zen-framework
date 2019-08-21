package org.zenframework.security;

/**
 * Created by Zeal on 2019/4/27 0027.
 */
public interface RememberMeAuthToken extends AuthenticationToken, RememberMe {

    /**
     * User decides to save remember me or not after successful logon.
     * @return
     */
    boolean isRememberMe();

}
