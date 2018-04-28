package com.yealink.security.authentication.provider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.yealink.security.authentication.model.UserInfo;

/**
 */
public class DeliverBearerAuthorizationProvider implements BearerAuthorizationProvider {

	@Override
	public String getAuthorization() {
		return null;
	}

	@Override
	public String getUserid() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = null;
		if (authentication != null && authentication.getPrincipal() != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserInfo) {
				userId = ((UserInfo) principal).getUserId();
			}
		}
		return userId;
	}
}
