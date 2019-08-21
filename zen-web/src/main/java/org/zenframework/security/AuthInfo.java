/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.security;

import java.util.Collection;
import java.util.Set;

/**
 * Authentication info and authorization info
 * @author Zeal 2019/01/13
 */
public interface AuthInfo {

    /**
     * Primary key in table, it can be integer or varchar
     * @return
     */
    Object getAuthInfoKey();

    /**
     * Credentials which is encrypted or plain
     * @return
     */
    String getCredentials();

    /**
     * Check whether current login failure count is >= MAX allowed time or not
     * @return
     */
    boolean isCaptchaRequired();

    /**
     * Indicate whether the account is locked or not
     * @return true it's locked
     */
    boolean isPrincipalsLocked();

    /**
     * Check whether the account is expired
     * @return
     */
    boolean isPrincipalsExpired();

    /**
     * Check whether the password is expired
     * @return
     */
    boolean isCredentialsExpired();

//    /**
//     * Clear sensitive info like credentials, leave principle, role, permission, remember me token will be enough
//     */
//    void clearSensitiveInfo();

    /**
     * Get roles
     * @return
     */
    Set<String> getRoles();

    /**
     * Set roles
     * @param roles
     */
    void setRoles(Collection<String> roles);

    /**
     * Only support string permissions to make it simple
     * @return
     */
    Set<String> getPermissions();

    /**
     * Set permissions
     * @param permissions
     */
    void setPermissions(Collection<String> permissions);

//    /**
//     * Get remember me token
//     * @return
//     */
//    RememberMeToken getRememberMeToken();
//
//    /**
//     * Set remember me token
//     * @param token
//     */
//    void setRememberMeToken(RememberMeToken token);

}
