package org.zenframework.security;

/**
 * Created by Zeal on 2019/4/26 0026.
 */
public interface RememberMe {

    /**
     * Remember me unique id, think about to combine with client id like IMEI, android id,mac id,uuid
     * @return
     */
    String getRememberMeSeries();

    /**
     * @param series
     */
    void setRememberMeSeries(String series);

    /**
     * Remember me access token
     * @return
     */
    String getRememberMeToken();

    /**
     * @param token
     */
    void setRememberMeToken(String token);
}
