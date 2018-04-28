package com.yealink.security.authentication.extractor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface PreAuthenticatedAuthenticationExtractor {

	public String getPrefix();

	public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request)
			throws AuthenticationException;
}
