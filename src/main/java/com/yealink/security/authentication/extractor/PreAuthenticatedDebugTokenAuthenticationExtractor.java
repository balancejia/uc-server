package com.yealink.security.authentication.extractor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.token.PreAuthenticatedDebugTokenAuthentication;
@Component
@Order(30)
public class PreAuthenticatedDebugTokenAuthenticationExtractor extends AbstractPreAuthenticatedAuthenticationExtractor {
    @Override
    public String getPrefix() {
        return TokenType.DEBUG.toString();
    }

    @Override
    public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request) throws AuthenticationException {
		// if(!SafContext.isDebugMode()) {
		// throw SafI18NException.of("当前非调试模式，无法使用。请使用配置: " +
		// SafContext.SAF_DEBUG_ENABLED + "=true");
		// }
        String debugToken = authenticationValue;
        Assert.hasText(debugToken,"Debug token is missing.");
        Map<String,String> map = splitToMap(debugToken);
        String userId = map.get("userid");
        Assert.hasText(userId,"Debug token property userid is missing.");
        String realm = map.get("realm");
        Assert.hasText(realm,"Debug token property realm is missing.");
        return new PreAuthenticatedDebugTokenAuthentication(realm,userId);
    }
}
