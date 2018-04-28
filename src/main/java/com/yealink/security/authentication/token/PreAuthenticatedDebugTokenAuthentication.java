package com.yealink.security.authentication.token;

public class PreAuthenticatedDebugTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5931795736376367321L;
	private String realm;
	private String userId;

	public PreAuthenticatedDebugTokenAuthentication(String realm, String userId) {
		this.realm = realm;
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public String getRealm() {
		return realm;
	}
}
