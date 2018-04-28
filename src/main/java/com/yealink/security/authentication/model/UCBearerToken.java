package com.yealink.security.authentication.model;

import java.util.Date;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UCBearerToken implements Token {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6964523750764507625L;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * bearer token
	 */
	private String bearerToken;
	/**
	 * 过期时间
	 */
	private Date expiresTime;
	/**
	 * 过期后用于刷新的token
	 */
	private String refreshToken;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public Date getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(Date expiresTime) {
		this.expiresTime = expiresTime;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public UCBearerToken() {
	}

	public UCBearerToken(String userId, String bearerToken, String refreshToken, Date expiresTime) {
		Assert.notNull(userId, "userId cannot be null.");
		Assert.notNull(bearerToken, "bearerToken cannot be null.");
		Assert.notNull(refreshToken, "refreshToken cannot be null.");
		this.userId = userId;
		this.bearerToken = bearerToken;
		this.refreshToken = refreshToken;
		this.expiresTime = expiresTime;
	}

	/**
	 *
	 * 判断bearer_token是否过期 @return boolean @throws
	 */
	@JsonIgnore
	public boolean isExpire() {
		Date start = new Date();
		Date end = getExpiresTime();
		return (end.getTime() - start.getTime()) < 0L;
	}
}
