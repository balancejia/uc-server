package com.yealink.model;

import javax.validation.constraints.NotNull;

public class RefreshTokenForm {
	
	
	@NotNull(message = "uc.token.userid.invalid")
	private String userId;
	
	@NotNull(message = "uc.token.accessToken.invalid")
	private String accessToken;
	
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
	
	

}
