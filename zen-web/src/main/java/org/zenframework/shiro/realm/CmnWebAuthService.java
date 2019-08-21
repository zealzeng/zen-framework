package org.zenframework.shiro.realm;

import org.zenframework.util.StringUtils;
import org.zenframework.shiro.vo.AuthInfo;
import org.zenframework.shiro.vo.LoginToken;
import org.zenframework.shiro.vo.RememberMeToken;
import org.zenframework.shiro.vo.WebAuthToken;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.zenframework.util.UUIDUtils;

import java.sql.*;

/**
 * CREATE TABLE `uc_login_token` (
  `login_token_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `series` char(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '''用户名密码登陆成功后如果开启remember me需要刷新,自动登陆成功则不变',
  `token` char(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '''用户名密码登陆成功后remember me需要刷新,自动登陆成功要刷新',
  `last_time` datetime NOT NULL COMMENT '最近修改时间',
  `deleted` tinyint(1) unsigned NOT NULL COMMENT '逻辑删除标记',
  PRIMARY KEY (`login_token_id`),
  UNIQUE KEY `series_UNIQUE` (`series`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='自动登陆';
 *
 * Created by Zeal on 2019/1/25 0025.
 */
public class CmnWebAuthService<B extends AuthInfo> extends WebAuthService<B> {

    public static final String CMN_REMEMBER_ME_BY_SERIES_QUERY = "select * from uc_login_token where series=?";

    public static final String CMN_ADD_REMEMBER_ME_SQL = "insert into uc_login_token(user_id,series,token,last_time,deleted)VALUES(?,?,?,?,?)";

    public static final String CMN_DELETE_REMEMBER_ME_SQL = "update uc_login_token set deleted=1 where series=?";

    public static final String CMN_UPDATE_REMEMBER_ME_SQL = "update uc_login_token set token=?,last_time=? where series=?";

    public static final String CMN_AUTH_INFO_BY_REMEMBER_ME_QUERY = "select * from uc_user where user_id=?";

    protected BeanPropertyRowMapper<? extends RememberMeToken> rememberMeTokenMapper = null;

    //Query remember me token by series
    private String rememberMeQuery = null;

    //Delete remember me token by series
    private String deleteRememberMeSql = null;

    //Refresh remember me token, update token,last_time from xx where series=
    private String updateRememberMeSql = null;

    //Query auth info by remember me
    private String rememberMeAuthInfoQuery = null;

    //Add remember me sql
    private String addRememberMeSql = null;

    public CmnWebAuthService() {
        this(true);
    }

    public CmnWebAuthService(boolean defaultRememberMe) {
        if (defaultRememberMe) {
            rememberMeQuery = CMN_REMEMBER_ME_BY_SERIES_QUERY;
            deleteRememberMeSql = CMN_DELETE_REMEMBER_ME_SQL;
            updateRememberMeSql = CMN_UPDATE_REMEMBER_ME_SQL;
            rememberMeAuthInfoQuery = CMN_AUTH_INFO_BY_REMEMBER_ME_QUERY;
            addRememberMeSql = CMN_ADD_REMEMBER_ME_SQL;
        }
        this.rememberMeTokenMapper = new BeanPropertyRowMapper(LoginToken.class);
    }

    public void setRememberMeTokenClass(Class rememberMeTokenClass) {
        this.rememberMeTokenMapper = new BeanPropertyRowMapper(rememberMeTokenClass);
    }

    public Class getRememberMeTokenClass() {
        return this.rememberMeTokenMapper.getMappedClass();
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

    @Override
    public B authenticateSuccess(WebAuthToken token, B authInfo) {
        return authInfo;
    }

    @Override
    public B authenticateFail(int errorCode, WebAuthToken token, B authInfo) {
        return authInfo;
    }

    @Override
    protected RememberMeToken addRememberMeToken(WebAuthToken token, AuthInfo authInfo) {
        if (StringUtils.isEmpty(this.addRememberMeSql) || this.rememberMeTokenMapper == null) {
            return null;
        }
        final String newToken = UUIDUtils.defaultUUID();
        final String newSeries = UUIDUtils.defaultUUID();
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(addRememberMeSql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, (Integer)authInfo.getAuthInfoKey());
                ps.setString(2, newSeries);
                ps.setString(3, newToken);
                ps.setTimestamp(4, now);
                ps.setInt(5, 0);
                return ps;
            }
        }, keyHolder);
        LoginToken loginToken = new LoginToken();
        loginToken.setDeleted(0);
        loginToken.setLastTime(now);
        loginToken.setLoginTokenId(keyHolder.getKey().intValue());
        loginToken.setSeries(newSeries);
        loginToken.setToken(newToken);
        loginToken.setUserId((Integer)authInfo.getAuthInfoKey());
        return loginToken;
    }

    @Override
    protected RememberMeToken queryRememberMeTokenBySeries(String series) {
        if (StringUtils.isEmpty(this.rememberMeQuery) || this.rememberMeTokenMapper == null) {
            return null;
        }
        return this.queryForObject(this.rememberMeQuery, this.rememberMeTokenMapper, series);
//        List<? extends RememberMeToken> list = this.getJdbcTemplate().query(this.rememberMeQuery, this.rememberMeTokenMapper, series);
//        if (list == null || list.size() <= 0) {
//            return null;
//        }
//        else {
//            return list.get(0);
//        }
    }

    @Override
    protected int updateRememberMeTokenBySeries(RememberMeToken rememberMeToken) {
        if (StringUtils.isNotEmpty(this.updateRememberMeSql)) {
            String newToken = UUIDUtils.defaultUUID();
            java.util.Date now = new java.util.Date();
            rememberMeToken.touch(rememberMeToken.getAuthInfoKey(), newToken, rememberMeToken.getSeries(), now);
            return this.getJdbcTemplate().update(this.updateRememberMeSql, newToken, now, rememberMeToken.getSeries());
        }
        else {
            return 0;
        }
    }

    @Override
    protected int deleteRememberMeBySeries(String series) {
        if (StringUtils.isNotEmpty(this.deleteRememberMeSql)) {
            return this.getJdbcTemplate().update(this.deleteRememberMeSql, series);
        }
        else {
            return 0;
        }
    }

    @Override
    protected B queryRememberMeAuthInfo(RememberMeToken rememberMeToken) {
        if (StringUtils.isEmpty(this.rememberMeAuthInfoQuery) || this.authInfoMapper == null) {
            return null;
        }
        return this.queryForObject(this.rememberMeAuthInfoQuery, this.authInfoMapper, (Integer) rememberMeToken.getAuthInfoKey());
//        List<B> list = this.getJdbcTemplate().query(this.rememberMeAuthInfoQuery, this.authInfoMapper, (Integer) rememberMeToken.getAuthInfoKey());
//        if (list == null || list.size() <= 0) {
//            return null;
//        }
//        else {
//            return list.get(0);
//        }
    }

}
