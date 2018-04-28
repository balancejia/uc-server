package com.yealink.security.authentication.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UCAuthentication extends AbstractAuthentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9035996972335981855L;

	private static final Logger logger = LoggerFactory.getLogger(UCAuthentication.class);

	private Object token;

	public UCAuthentication(List<? extends GrantedAuthority> authorities) {
		super(authorities);
	}

	public UCAuthentication(UCUserDetails ucUserDetails) {
		this(ucUserDetails.getAuthorities());
		setDetails(ucUserDetails);
		setAuthenticated(true);
	}

	public UCAuthentication(UCUserDetails ucUserDetails, Object token) {
		this(ucUserDetails);
		this.token = token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	/**
	 * 返回用户主体信息,主体信息中包含用户信息 userinfo 和角色信息
	 */
	@Override
	public Object getPrincipal() {
		UCUserDetails ucUserDetails = (UCUserDetails) super.getDetails();
		if (null != ucUserDetails) {
			return ucUserDetails.getUserInfo();
		}
		return null;
	}

	public Object getToken() {
		return token;
	}
	public void setToken(Object token) {
		this.token = token;
	}
}
