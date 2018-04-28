package com.yealink.security.authentication.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.model.UCAuthentication;
import com.yealink.security.authentication.model.UCUserDetails;
import com.yealink.security.authentication.service.IUserDetailsService;
import com.yealink.security.authentication.token.PreAuthenticatedDebugTokenAuthentication;

/**
 * DEBUG TOKEN授权提供类
 */
@Component
@Order(30)
public class DebugTokenAuthenticationProvider implements AuthenticationProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private IUserDetailsService safUserDetailsService;

	@Autowired
	public void setSafUserDetailsService(IUserDetailsService safUserDetailsService) {
		this.safUserDetailsService = safUserDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.debug("Debug token authenticate begin");
		Assert.notNull(authentication, "authentication cannot be null.");
		// if(!SafContext.isDebugMode()) {
		// throw SafI18NException.of("当前非调试模式，无法使用。请使用配置[saf.debug.enabled=true]",
		// SecurityErrorCode.SAF_DEBUG_ENABLED);
		// }
		PreAuthenticatedDebugTokenAuthentication preAuthenticatedDebugTokenAuthentication = (PreAuthenticatedDebugTokenAuthentication) authentication;
		logger.debug("get userDetials begin");
		UCUserDetails ucUserDetails = safUserDetailsService
				.loadUserDetailsByUserId(preAuthenticatedDebugTokenAuthentication.getUserId());
		logger.debug("get userDetials end");
		ucUserDetails.getUserInfo().setUserType(TokenType.DEBUG.toString());
		logger.debug("Debug token authenticate end");

		return new UCAuthentication(ucUserDetails);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return false;
	}
}
