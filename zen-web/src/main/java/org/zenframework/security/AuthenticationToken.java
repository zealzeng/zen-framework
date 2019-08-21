package org.zenframework.security;

/**
 * Use to authenticate, like user name and password
 * Created by Zeal on 2019/4/17 0017.
 */
public interface AuthenticationToken {

    /**
     * Authentication principal identity like user name
     * @return
     */
    String getPrincipal();

    /**
     * Authentication credentials like password
     * @return
     */
    String getCredentials();

}
