package com.yealink.security.authentication.token;

import org.springframework.util.Assert;

public class PreAuthenticatedBearerTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6112476286934665999L;
	private String bearerToken;
	private String userId;

	public PreAuthenticatedBearerTokenAuthentication(String bearerToken,String userId) {
		Assert.hasText(bearerToken, "Bearer token should contains text.");
		this.bearerToken = bearerToken;
		this.userId = userId;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public String getUserId() {
		return userId;
	}

}
