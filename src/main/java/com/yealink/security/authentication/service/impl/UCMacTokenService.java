package com.yealink.security.authentication.service.impl;

import java.util.Date;
import java.util.Hashtable;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.catalina.Host;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.SecurityErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.EncrypUtils;
import com.yealink.security.authentication.constant.TokenConstant;
import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.model.UCAccount;
import com.yealink.security.authentication.model.UCMacToken;
import com.yealink.security.authentication.service.IAuthorizeInfoService;
import com.yealink.security.authentication.service.TokenService;
import com.yealink.security.authentication.token.PreAuthenticatedMacTokenAuthentication;
import com.yealink.uc.entity.AuthorizeInfo;

/**
 * Mac Token的验证服务。
 */
@Service
public class UCMacTokenService implements TokenService<UCMacToken> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IAuthorizeInfoService authorizeInfoService;

	@Override
	public UCMacToken getToken(UCAccount ucAccount) {
		return null;
	}

	/**
	 * 根据传入进来的token头信息，构建请求，去验证token的正确性 其中key为UC验证所需要的请求信息的json字符串
	 * 如果返回的token不为空，则证明token验证成功。 如果返回时空，说明验证失败，并且有异常错误信息存在。 错误信息存放在全局变量Exception中。
	 */
	@Override
	public UCMacToken verifyToken(Authentication preAuthenticatedAuthentication) {
		Assert.notNull(preAuthenticatedAuthentication, "preAuthenticatedAuthentication cannot be null.");
		PreAuthenticatedMacTokenAuthentication authentication = (PreAuthenticatedMacTokenAuthentication) preAuthenticatedAuthentication;
		AuthorizeInfo authorizeInfo = authorizeInfoService.getAuthorizeInfo(authentication.getId());
		if (authorizeInfo == null) {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_INVALID);
		}else if (!authorizeInfo.getTokenType().equals(TokenType.MAC.name())) {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_INVALID);
		}
		// 认证成功
		UCMacToken ucMacToken = new UCMacToken();
		ucMacToken.setMacKey(authorizeInfo.getSignKey());
		ucMacToken.setAccessToken(authorizeInfo.getAccessToken());
		ucMacToken.setUserId(authorizeInfo.getUserId().toString());
		ucMacToken.setExpiresTime(new Date(authorizeInfo.getExpiresTime()));
		ucMacToken.setRefreshToken(authorizeInfo.getRefreshToken());
		ucMacToken.setServerTime(new Date(System.currentTimeMillis()));
		if (ucMacToken.isExpire()) {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_EXPIRED);
		}
		
		return checkMac(ucMacToken, authentication);
	}

	private UCMacToken checkMac(UCMacToken macToken, PreAuthenticatedMacTokenAuthentication authentication) {
		Assert.notNull(macToken, "macToken cannot be null.");
		Assert.notNull(authentication, "authentication cannot be null");
		
		String sbRawMac = authentication.getNonce() + "\n" + authentication.getHttpMethod().toUpperCase() + "\n"
				+ authentication.getRequestUri() + "\n" + authentication.getHost() + "\n";
		String newMac = EncrypUtils.encryptHMac256(sbRawMac, macToken.getMacKey());
		if (logger.isDebugEnabled()) {
			logger.debug("Mac key:{}, newMac:{}", authentication.getMac(), newMac);
		}
		if (authentication.getMac().equalsIgnoreCase(newMac)) {
			return macToken;
		} else {
			throw UCException.of(SecurityErrorCode.MAC_SIGN_INVALID);
		}
	}

	@Override
	public UCMacToken refreshToken(UCMacToken token) {
		AuthorizeInfo authorizeInfo = authorizeInfoService.getAuthorizeInfo(token.getAccessToken());
		if (authorizeInfo != null && authorizeInfo.getTokenType().equals(TokenType.MAC.name()) 
				&& token.getRefreshToken().equals(authorizeInfo.getRefreshToken()) && authorizeInfo.getExpiresTime() > System.currentTimeMillis()) {
			token = buildToken(authorizeInfo.getUserId().toString());
			AuthorizeInfo updateInfo = new AuthorizeInfo();
			updateInfo.setId(authorizeInfo.getId());
			updateInfo.setAccessToken(token.getAccessToken());
			updateInfo.setExpiresTime(token.getExpiresTime().getTime());
			updateInfo.setSignKey(token.getMacKey());
			updateInfo.setRefreshToken(token.getRefreshToken());
			updateInfo.setTokenType(TokenType.MAC.name());
			authorizeInfoService.updateAuthorizeRelation(updateInfo);
			return token;
		}
		throw new UCException(ErrorCode.UC_TOKEN_REFRESHTOKEN_FAIL);
	}

	/**
	 * 创建token
	 * @param userId
	 * @return
	 */
	private UCMacToken buildToken(String userId) {
		UCMacToken ucMacToken = new UCMacToken();
		long currentTime = System.currentTimeMillis();
		ucMacToken.setMacKey(RandomStringUtils.randomAlphanumeric(8));
		ucMacToken.setAccessToken(RandomStringUtils.randomAlphanumeric(32));
		ucMacToken.setUserId(userId);
		ucMacToken.setRefreshToken(RandomStringUtils.randomAlphanumeric(32));
		ucMacToken.setExpiresTime(new Date((currentTime + TokenConstant.DEFAULT_ACCESS_TOKEN_VALIDITY * 1000L)));
		ucMacToken.setServerTime(new Date(currentTime));
		
		return ucMacToken;
	}

	@Override
	public UCMacToken createToken(UCAccount ucAccount) {

		UCMacToken ucMacToken = buildToken(ucAccount.getUserId());
		AuthorizeInfo authorizeInfo = new AuthorizeInfo();
		authorizeInfo.setUserId(Long.valueOf(ucAccount.getUserId()));
		authorizeInfo.setExpiresTime(ucMacToken.getExpiresTime().getTime());
		authorizeInfo.setAccessToken(ucMacToken.getAccessToken());
		authorizeInfo.setRefreshToken(ucMacToken.getRefreshToken());
		authorizeInfo.setSignKey(ucMacToken.getMacKey());
		authorizeInfo.setCreateTime(new Date());
		authorizeInfoService.replaceMacAuthorizeRelation(authorizeInfo);
		return ucMacToken;
	}
	
	
}
