package com.yealink.model;

import javax.validation.constraints.NotNull;

public class MacTokenForm {
	
	@NotNull(message = "uc.token.username.invalid")
	private String mac;
	
	@NotNull(message = "auth.nonce.invalid")
	private String nonce;
	
	@NotNull(message = "uc.token.username.invalid")
	private String httpMethod;
	
	@NotNull(message = "uc.token.username.invalid")
	private String requestUri;
	
	@NotNull(message = "uc.token.username.invalid")
	private String host;
	
	@NotNull(message = "uc.token.username.invalid")
	private String userId;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	public String getRequestUri() {
		return requestUri;
	}
	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
