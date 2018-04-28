package com.yealink.security.authentication.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UCMacToken implements Token {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6371004800768733393L;
	/**
	 * 认证后的用户ID
	 */
	private String userId;
	/**
	 * 验证后的token
	 */
	private String accessToken;
	/**
	 * 过期刷新用的token
	 */
	private String refreshToken;
	/**
	 * 过期时间
	 */
	private Date expiresTime;
	/**
	 * 服务器返回时间
	 */
	private Date serverTime;
	/**
	 * hmac的秘钥
	 */
	private String macKey;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(Date expiresTime) {
		this.expiresTime = expiresTime;
	}

	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	public String getMacKey() {
		return macKey;
	}

	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}

	@JsonIgnore
	public boolean isExpire() {
		Date start = new Date();
		Date end = getExpiresTime();
		return end.getTime() - start.getTime() < 0L;
	}
}
