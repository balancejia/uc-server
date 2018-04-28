package com.yealink.security.authentication.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.model.UCAuthentication;
import com.yealink.security.authentication.model.UCMacToken;
import com.yealink.security.authentication.model.UCUserDetails;
import com.yealink.security.authentication.service.IUserDetailsService;
import com.yealink.security.authentication.service.TokenService;
import com.yealink.security.authentication.token.PreAuthenticatedMacTokenAuthentication;

/**
 * MAC TOKEN授权提供类
 */
@Component
@Order(20)
public class MacTokenAuthenticationProvider implements AuthenticationProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(MacTokenAuthenticationProvider.class);
	private IUserDetailsService uCUserDetailsService;
	private TokenService<UCMacToken> macTokenService;

	@Autowired
	public void setTokenService(@Qualifier("mac_token_service") TokenService<UCMacToken> tokenService) {
		this.macTokenService = tokenService;
	}

	@Autowired
	public void setUCUserDetailsService(IUserDetailsService ucUserDetailsService) {
		this.uCUserDetailsService = ucUserDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		logger.debug("Mac token authenticate begin.");
		Assert.notNull(authentication, "authentication cannot be null.");
		PreAuthenticatedMacTokenAuthentication macTokenAuthentication = (PreAuthenticatedMacTokenAuthentication) authentication;
		UCMacToken uCMacToken = (UCMacToken) macTokenService.verifyToken(macTokenAuthentication);
		Assert.notNull(uCMacToken, "safUCToken cannot be null.");
		UCUserDetails ucUserDetails = uCUserDetailsService.loadUserDetailsByUserId(uCMacToken.getUserId());
		Assert.notNull(ucUserDetails, "ucUserDetails cannot be null.");
		ucUserDetails.getUserInfo().setUserType(TokenType.MAC.toString());
		logger.debug("Mac token authenticate end.");
		return new UCAuthentication(ucUserDetails, uCMacToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == PreAuthenticatedMacTokenAuthentication.class;
	}
}
