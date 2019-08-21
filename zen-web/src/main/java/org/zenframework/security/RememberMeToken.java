package org.zenframework.security;

import java.util.Date;

/**
 * Created by Zeal on 2019/1/24 0024.
 */
public interface RememberMeToken extends RememberMe {

    /**
     * Auth info primary key, integer or varchar
     * @return
     */
    Object getAuthInfoKey();

    /**
     * Set auth info pk
     * @param key
     */
    void setAuthInfoKey(Object key);

    /**
     * Last login time or access time
     * @return
     */
    Date getLastTime();

    /**
     * Set last time
     * @param lastTime
     */
    void setLastTime(Date lastTime);
}
