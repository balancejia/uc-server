package com.yealink.security.authentication.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.model.UCAuthentication;
import com.yealink.security.authentication.model.UCBearerToken;
import com.yealink.security.authentication.model.UCUserDetails;
import com.yealink.security.authentication.service.IUserDetailsService;
import com.yealink.security.authentication.service.TokenService;
import com.yealink.security.authentication.token.PreAuthenticatedBearerTokenAuthentication;

/**
 * BEARER TOKEN授权提供类
 *
 */
@Component
@Order(10)
public class BearerTokenAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory.getLogger(BearerTokenAuthenticationProvider.class);
	private IUserDetailsService safUserDetailsService;
	private TokenService<UCBearerToken> bearerTokenService;

	@Autowired
	public void setSafUserDetailsService(IUserDetailsService safUserDetailsService) {
		this.safUserDetailsService = safUserDetailsService;
	}

	@Autowired
	public void setBearerTokenService(@Qualifier("bearer_token_service") TokenService<UCBearerToken> tokenService) {
		this.bearerTokenService = tokenService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.debug("Bearer token authenticate begin.");

		Assert.notNull(authentication, "authentication cannot be null.");
		PreAuthenticatedBearerTokenAuthentication bearerTokenAuthentication = (PreAuthenticatedBearerTokenAuthentication) authentication;
		Assert.notNull(bearerTokenAuthentication, "bearerTokenAuthentication cannot be null.");
		UCBearerToken safUCToken = (UCBearerToken) bearerTokenService.verifyToken(bearerTokenAuthentication);
		Assert.notNull(safUCToken, "safUCToken cannot be null.");
		String userId = safUCToken.getUserId();
		UCUserDetails ucUserDetails = safUserDetailsService.loadUserDetailsByUserId(userId);
		Assert.notNull(ucUserDetails, "ucUserDetails cannot be null.");
		ucUserDetails.getUserInfo().setUserType(TokenType.BEARER.toString());
		logger.debug("Bearer token authenticate end.");
		return new UCAuthentication(ucUserDetails, safUCToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == PreAuthenticatedBearerTokenAuthentication.class;
	}
}
