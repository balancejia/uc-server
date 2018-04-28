package com.yealink.security.authentication.extractor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.provider.DeliverBearerAuthorizationProvider;
import com.yealink.security.authentication.token.PreAuthenticatedBearerTokenAuthentication;

@Component
@Order(30)
public class PreAuthenticatedBearerTokenAuthenticationExtractor
		extends AbstractPreAuthenticatedAuthenticationExtractor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getPrefix() {
		return TokenType.BEARER.toString();
	}

	@Override
	public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request)
			throws AuthenticationException {

		Assert.hasText(authenticationValue, "Bearer token is missing.");
		
		Map<String, String> map = splitToMap(authenticationValue);
		String userId = getValue(map, "userId");
		String bearerToken = getValue(map, "token");
		Assert.hasText(userId, "userId is missing.");
		Assert.hasText(bearerToken, "bearerToken is missing.");
		
		logger.debug("bearer token:{},userId:{}", bearerToken,userId);
		return new PreAuthenticatedBearerTokenAuthentication(bearerToken, userId);
		
/*		String bearerToken = StringUtils.strip(authenticationValue, "\"");
		Assert.hasText(bearerToken, "Bearer token is missing.");
		String userId = request.getHeader(DeliverBearerAuthorizationProvider.USERID);
		logger.debug("bearer token:{},userId:{}", bearerToken,userId);
		return new PreAuthenticatedBearerTokenAuthentication(bearerToken, userId);*/
	}
}
