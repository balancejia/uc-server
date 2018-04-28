package com.yealink.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.yealink.cache.local.ILocalCache;
import com.yealink.security.authentication.extractor.PreAuthenticatedAuthenticationExtractorManager;
import com.yealink.security.authentication.service.impl.UCBearerTokenService;
import com.yealink.security.authentication.service.impl.UCMacTokenService;
import com.yealink.security.authentication.service.impl.UCUserDetailsService;
import com.yealink.security.filter.TokenAuthenticationProcessFilter;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	protected SecurityConfig() {
		super(true);
	}

	@Override
	public void init(WebSecurity web) throws Exception {
		// 不经过认证过滤器
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers(HttpMethod.POST, "/api/v1/tokens")
				.antMatchers(HttpMethod.POST, "/api/v1/bearer_tokens")
				.antMatchers(HttpMethod.POST, "/api/v1/tokens/mac/*/valid")
				.antMatchers(HttpMethod.POST, "/api/v1/tokens/bearer/*/valid");
//			.antMatchers("/api/v1/**");

		super.init(web);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, List<AuthenticationProvider> providers) {
		for (AuthenticationProvider provider : providers) {
			auth.authenticationProvider(provider);
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().
		// rest is stateless, no sessions
		sessionManagement().sessionCreationPolicy((SessionCreationPolicy.STATELESS)).and().securityContext()
		.and().addFilterAfter(tokenAuthenticationProcessFilter(), SecurityContextPersistenceFilter.class);
		// 经过认证过滤器
		http.authorizeRequests()
			//用户相关	
			.antMatchers(HttpMethod.GET,"/api/v1/users").hasAnyAuthority("P_USER_QUERY")
			.antMatchers(HttpMethod.POST, "/api/v1/users").hasAuthority("P_USER_UPDATE")
			.antMatchers(HttpMethod.PUT, "/api/v1/users/*").hasAuthority("P_USER_UPDATE")
			.antMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasAuthority("P_USER_UPDATE")
			//授权相关
			.antMatchers("/api/v1/user_roles", "/api/v1/role_permissions","/api/v1/group_permissions", "/api/v1/users/*/roles", "/api/v1/roles/**", "api/v1/permissions/**")
			.hasAuthority("P_PERMISSION_MANAGER")
			//系统业务相关
			.antMatchers("/apis/v1/auths/*/valid","/apis/vi/users/*")
			.hasAuthority("P_AUTHENTICATION_BIZ")
			//AD域同步相关
			.antMatchers(HttpMethod.GET,"/api/v1/user_groups/actions/adSync").hasAnyAuthority("P_AD_SYNC")
			.antMatchers(HttpMethod.GET,"/api/v1/users/actions/adSync").hasAnyAuthority("P_AD_SYNC")
			
			.anyRequest().authenticated();
	}

	/**
	 * 提供从请求获取认证对象的Extractor管理器
	 *
	 * @return
	 */
	@Bean
	public PreAuthenticatedAuthenticationExtractorManager preAuthenticatedAuthenticationExtractorManager() {
		return new PreAuthenticatedAuthenticationExtractorManager();
	}

	@Bean
	UCUserDetailsService customUserService() {
		return new UCUserDetailsService();
	}

	@Bean("mac_token_service")
	UCMacTokenService uCMacTokenService() {
		return new UCMacTokenService();
	}

	@Bean("bearer_token_service")
	UCBearerTokenService uCBearerTokenService() {
		return new UCBearerTokenService();
	}

	protected TokenAuthenticationProcessFilter tokenAuthenticationProcessFilter() throws Exception {
		return new TokenAuthenticationProcessFilter(super.authenticationManager(),
				preAuthenticatedAuthenticationExtractorManager());
	}

}
