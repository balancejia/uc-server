package com.yealink.security.authentication.constant;

public interface TokenConstant {

	/** 访问令牌默认有效期（秒） */
	long DEFAULT_ACCESS_TOKEN_VALIDITY = 3600;

	/** 刷新令牌默认有效期（秒） */
	long DEFAULT_REFRESH_TOKEN_VALIDITY = 24 * 3600;

}
