///*
// * Copyright (c) 2017, All rights reserved.
// */
//package org.zenframework.service.impl;
//
//import java.util.List;
//
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import org.zenframework.service.OAuth2Service;
//import org.zenframework.web.vo.AccessToken;
//
///**
// * @author Zeal
//
//CREATE TABLE `oauth_access_token` (
//  `uuid` varchar(32) NOT NULL COMMENT '前16位用于签名,标记手机终端唯一编号',
//  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
//  `access_token` varchar(32) NOT NULL COMMENT '采访令牌环',
//  `access_time` datetime NOT NULL COMMENT '最近采访时间',
//  `refresh_token` varchar(32) NOT NULL COMMENT '刷新令牌环',
//  `expire_time` datetime NOT NULL COMMENT '超时时间',
//  PRIMARY KEY (`uuid`,`user_id`),
//  UNIQUE KEY `access_token_UNIQUE` (`access_token`),
//  UNIQUE KEY `refresh_token_UNIQUE` (`refresh_token`)
//) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采访令牌环';
//
//
// * @since 2017年5月27日
// * @deprecated
// */
//public abstract class JdbcOAuth2Service implements OAuth2Service {
//
//	protected JdbcTemplate jdbcTemplate = null;
//
//	protected BeanPropertyRowMapper<AccessToken> beanMapper = new BeanPropertyRowMapper<AccessToken>(AccessToken.class);
//
//
//	/**
//	 * Get access token record by access_token
//	 * @param accessToken
//	 * @return
//	 */
//	public AccessToken getAccessToken(String accessToken) {
//		String sql = "select * from oauth_access_token where access_token=?";
//		List<AccessToken> list = this.jdbcTemplate.query(sql, beanMapper, accessToken);
//		if (list != null && list.size() > 0) {
//			return list.get(0);
//		}
//		else {
//			return null;
//		}
//	}
//
//	/**
//	 * @param uuid
//	 * @return
//	 */
//	public AccessToken getAccessTokenByUuid(String uuid, String userId) {
//		String sql = "select * from oauth_access_token where uuid=? and user_id=?";
//		List<AccessToken> list = this.jdbcTemplate.query(sql, beanMapper, uuid, userId);
//		if (list != null && list.size() > 0) {
//			return list.get(0);
//		}
//		else {
//			return null;
//		}
//	}
//
//	/**
//	 * @param uuid
//	 * @return
//	 */
//	public AccessToken getAccessTokenByRefreshToken(String refreshToken) {
//		String sql = "select * from oauth_access_token where refresh_token=?";
//		List<AccessToken> list = this.jdbcTemplate.query(sql, beanMapper, refreshToken);
//		if (list != null && list.size() > 0) {
//			return list.get(0);
//		}
//		else {
//			return null;
//		}
//	}
//
//	/**
//	 * @param accessToken
//	 * @return
//	 */
//	public int addAccessToken(AccessToken accessToken) {
//		String sql = "INSERT INTO `oauth_access_token`" +
//	        "(`uuid`,`user_id`,`access_token`,`access_time`,`refresh_token`,`expire_time`) VALUES(?,?,?,?,?,?)";
//		return this.jdbcTemplate.update(sql, accessToken.getUuid(), accessToken.getUserId(), accessToken.getAccessToken(),
//			accessToken.getAccessTime(), accessToken.getRefreshToken(), accessToken.getExpireTime());
//	}
//
//	/**
//	 * @param accessToken
//	 * @return
//	 */
//	public int updateAccessToken(AccessToken accessToken) {
//		String sql = "update oauth_access_token set access_token=?,access_time=?,refresh_token=?,expire_time=? where uuid=? and user_id=?";
//		return this.jdbcTemplate.update(sql, accessToken.getAccessToken(), accessToken.getAccessTime(),
//			accessToken.getRefreshToken(), accessToken.getExpireTime(), accessToken.getUuid(), accessToken.getUserId());
//	}
//
//	/**
//	 * @return the jdbcTemplate
//	 */
//	public JdbcTemplate getJdbcTemplate() {
//		return jdbcTemplate;
//	}
//
//	/**
//	 * @param jdbcTemplate the jdbcTemplate to set
//	 */
//	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
//		this.jdbcTemplate = jdbcTemplate;
//	}
//
//}
