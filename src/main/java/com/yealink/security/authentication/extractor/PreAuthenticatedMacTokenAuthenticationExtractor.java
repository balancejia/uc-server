package com.yealink.security.authentication.extractor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.SecurityErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.token.PreAuthenticatedMacTokenAuthentication;

@Component
@Order(30)
public class PreAuthenticatedMacTokenAuthenticationExtractor extends AbstractPreAuthenticatedAuthenticationExtractor {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ILocalCache<String, Byte> nonceCache;

	@Override
	public String getPrefix() {
		return TokenType.MAC.toString();
	}

	@Override
	public Authentication extractAuthentication(String authenticationValue, HttpServletRequest request)
			throws AuthenticationException {

		String macToken = authenticationValue;
		Assert.hasText(macToken, "Mac token is missing.");
		String host = request.getHeader(HttpHeaders.HOST);
		Assert.hasText(host, "请求头部信息host 不能为空.");
		String requestURI = this.getRequestURL(request);
		requestURI = this.getURI(host, requestURI);
		logger.debug("requestURI:{}, host:{}", requestURI, host);

		Map<String, String> map = splitToMap(macToken);

		String id = getValue(map, "id");
		String nonce = getValue(map, "nonce");
		String mac = getValue(map, "mac");

		Assert.hasText(id, "Mac token property id is missing.");
		Assert.hasText(nonce, "Mac token property nonce is missing.");
		Assert.hasText(mac, "Mac token property mac is missing.");

		//如果nonce已经在缓存内，抛出token过期异常
		if (nonceCache.getIfPresent(nonce) == null){
			nonceCache.put(nonce, (byte) 1);
		}else {
			throw UCException.of(SecurityErrorCode.AUTH_TOKEN_EXPIRED);
		}
		
		logger.debug("mac:{}, id:{}, nonce:{}", mac, id, nonce);
		return new PreAuthenticatedMacTokenAuthentication(id, mac, nonce, request.getMethod(), requestURI, host);
	}

	private String getRequestURL(HttpServletRequest request) {
		String reqString = request.getRequestURL().toString();
		String queryStr = request.getQueryString();
		// 判断请求参数是否为空
		if (!StringUtils.isEmpty(queryStr)) {
			reqString = reqString + "?" + queryStr;
		}
		return reqString;
	}

	private String getURI(String host, String url) {
		// 需要考虑使用ip地址的时候端口号的问题
		int index = url.indexOf(host);
		if (index == -1) {
			return url;
		}
		return url.substring(index + host.length());
	}
}
