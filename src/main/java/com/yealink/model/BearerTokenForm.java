package com.yealink.model;

import javax.validation.constraints.NotNull;

public class BearerTokenForm {

	@NotNull(message = "uc.token.userid.invalid")
	private String userId;

	@NotNull(message = "uc.token.accessToken.invalid")
	private String bearerToken;

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

	

}
