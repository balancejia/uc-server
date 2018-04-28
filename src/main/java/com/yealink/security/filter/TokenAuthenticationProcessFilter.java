package com.yealink.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yealink.common.exception.SecurityErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.security.authentication.extractor.PreAuthenticatedAuthenticationExtractorManager;

/**
 * 自定义的Mactoken的认证处理Filter对象。其扩展自{@link GenericFilterBean}抽象类。<br>
 * 1、配置 AuthenticationEntryPoint <br>
 * 2、获取请求Authoritation头信息，并且解析 <br>
 * 3、通过Provider进行认证处理<br>
 * 4、认证后获取用户信息<br>
 */
public class TokenAuthenticationProcessFilter extends OncePerRequestFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 授权头参数名
	 */
	public final static String AUTH_HEADER = "Authorization";

	private AuthenticationManager authenticationManager;

	private PreAuthenticatedAuthenticationExtractorManager extractorManager;

	public TokenAuthenticationProcessFilter(AuthenticationManager authenticationManager,
			PreAuthenticatedAuthenticationExtractorManager extractorManager) {
		this.authenticationManager = authenticationManager;
		this.extractorManager = extractorManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, IllegalArgumentException {
		logger.debug("Token authentication filter start.");

		String authenrization = request.getHeader(AUTH_HEADER);
		logger.debug("Authorization: {}", authenrization);

		if (!StringUtils.isBlank(authenrization)) {
			long beginTime = System.currentTimeMillis();
			logger.debug("Authorization beginTime:{}", beginTime);

			try {
				Authentication authentication = extractorManager.extractAuthentication(authenrization, request);
				if (null != authentication) {
					Authentication successAuthentication = authenticationManager.authenticate(authentication);
					SecurityContextHolder.getContext().setAuthentication(successAuthentication);
				}
				
			} catch (AuthenticationException ex) {
				SecurityContextHolder.clearContext();
				throw  UCException.of(SecurityErrorCode.FORBIDDEN);
			} catch (IllegalArgumentException ex) {
				throw ex;
			} catch (UCException ucex) {
				throw ucex;
			} catch (Exception ex) {
				throw UCException.of(SecurityErrorCode.SAF_ER_AUTHORIZED_EXCEPTION);
			} finally {
				long endTime = System.currentTimeMillis();
				logger.debug("Authorization endTime:{}, the total time:{}ms", endTime, endTime - beginTime);
			}
		}
		filterChain.doFilter(request, response);
		logger.debug("Token authentication filter end.");
	}
}
