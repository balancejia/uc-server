package com.yealink.security.authentication.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AbstractPreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2850801332555391200L;

	public AbstractPreAuthenticatedAuthenticationToken() {
		super(null);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}
}
