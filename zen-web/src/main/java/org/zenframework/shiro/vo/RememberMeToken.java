package org.zenframework.shiro.vo;

import java.util.Date;

/**
 * Created by Zeal on 2019/1/24 0024.
 */
public interface RememberMeToken {

    /**
     * Auth info primary key
     * @return
     */
    Object getAuthInfoKey();

    /**
     * Login token
     * @return
     */
    String getToken();

    /**
     * Login series
     * @return
     */
    String getSeries();

    /**
     * Last login time
     * @return
     */
    Date getLastTime();

    /**
     * Update token and time after login success
     * @param newToken
     * @param newLastTime
     */
    void touch(Object authInfoKey, String newToken, String series, Date newLastTime);

    /**
     * Leave token and series values will be enough
     */
    void clearSensitiveInfo();

}
