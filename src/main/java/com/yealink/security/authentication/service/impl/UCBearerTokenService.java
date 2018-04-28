package com.yealink.security.authentication.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.SecurityErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.EncrypUtils;
import com.yealink.security.authentication.constant.TokenConstant;
import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.model.UCAccount;
import com.yealink.security.authentication.model.UCBearerToken;
import com.yealink.security.authentication.model.UCMacToken;
import com.yealink.security.authentication.service.IAuthorizeInfoService;
import com.yealink.security.authentication.service.TokenService;
import com.yealink.security.authentication.token.PreAuthenticatedBearerTokenAuthentication;
import com.yealink.uc.entity.AuthorizeInfo;

/**
 * Bearer token 验证service
 */
public class UCBearerTokenService implements TokenService<UCBearerToken> {

	private static final String BEARE_KEY = "s,aj3bmx,g2kl";

	@Resource
	private IAuthorizeInfoService authorizeInfoService;

	@Override
	public UCBearerToken getToken(UCAccount ucAccount) {
		// TODO
		return null;
	}
	@Override
	public UCBearerToken verifyToken(Authentication preAuthenticatedAuthentication) {
		Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication cannot be null.");
		PreAuthenticatedBearerTokenAuthentication authentication = (PreAuthenticatedBearerTokenAuthentication) preAuthenticatedAuthentication;
		AuthorizeInfo authorizeInfo = authorizeInfoService.getAuthorizeInfo(authentication.getBearerToken());
		if (authorizeInfo == null) {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_INVALID);
		}else if(!authorizeInfo.getTokenType().equals(TokenType.BEARER.name())||!authorizeInfo.getUserId().toString().equals(authentication.getUserId())) {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_INVALID);
		}
		UCBearerToken ucBearerToken = new UCBearerToken();
		ucBearerToken.setBearerToken(authorizeInfo.getAccessToken());
		ucBearerToken.setExpiresTime(new Date(authorizeInfo.getExpiresTime()));
		ucBearerToken.setRefreshToken(authorizeInfo.getRefreshToken());
		ucBearerToken.setUserId(authentication.getUserId());
		if(ucBearerToken.isExpire()) {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_EXPIRED);
		}
		return ucBearerToken;
		
	}

	@Override
	public UCBearerToken refreshToken(UCBearerToken token) {
		AuthorizeInfo authorizeInfo = authorizeInfoService.getAuthorizeInfo(token.getBearerToken());
		long currentTime = System.currentTimeMillis();
		if (authorizeInfo != null && authorizeInfo.getTokenType().equals(TokenType.BEARER.name()) 
				&& token.getRefreshToken().equals(authorizeInfo.getRefreshToken()) && authorizeInfo.getExpiresTime() > currentTime) {
			
//			String accessToken = EncrypUtils.encryptHMac256(authorizeInfo.getUserId() + "\n" + currentTime, BEARE_KEY);
			String accessToken = RandomStringUtils.randomAlphanumeric(32);
			String refreshToken = RandomStringUtils.randomAlphanumeric(32);
			long expireTime = (currentTime + TokenConstant.DEFAULT_ACCESS_TOKEN_VALIDITY * 1000L);
			token.setUserId(authorizeInfo.getUserId().toString());
			token.setBearerToken(accessToken);
			token.setRefreshToken(refreshToken);
			token.setExpiresTime(new Date(expireTime));
			AuthorizeInfo updateInfo = new AuthorizeInfo();
			updateInfo.setId(authorizeInfo.getId());
			updateInfo.setAccessToken(accessToken);
			updateInfo.setRefreshToken(refreshToken);
			updateInfo.setExpiresTime(expireTime);
			authorizeInfoService.updateAuthorizeRelation(updateInfo);
			return token;

		}
		throw new UCException(ErrorCode.UC_TOKEN_REFRESHTOKEN_FAIL);
	}
	@Override
	public UCBearerToken createToken(UCAccount ucAccount) {
		long currentTime = System.currentTimeMillis();
		String refreshToken = RandomStringUtils.randomAlphanumeric(32);
//		String accessToken = EncrypUtils.encryptHMac256(ucAccount.getUserId() + "\n" + currentTime, BEARE_KEY);
		String accessToken = RandomStringUtils.randomAlphanumeric(32);
		long expireTime = (currentTime + TokenConstant.DEFAULT_ACCESS_TOKEN_VALIDITY * 1000L);
		UCBearerToken token = new UCBearerToken(ucAccount.getUserId(), accessToken, refreshToken, new Date(expireTime));
		AuthorizeInfo authorizeInfo = new AuthorizeInfo();
		authorizeInfo.setAccessToken(accessToken);
		authorizeInfo.setUserId(Long.valueOf(ucAccount.getUserId()));
		authorizeInfo.setRefreshToken(refreshToken);
		authorizeInfo.setExpiresTime(expireTime);
		authorizeInfo.setCreateTime(new Date());
		authorizeInfo.setTokenType(TokenType.BEARER.name());
		authorizeInfoService.insertAuthorizeRelation(authorizeInfo);
		return token;
	}
	
}
