package com.yealink.security.authentication.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * 用户中心用户详细信息的详细数据封装对象。其实现UCUserDetails接口
 *
 */
public class UCUserDetails {
	private UserInfo userInfo;

	List<GrantedAuthority> grantedAuthorities;
	
	public UCUserDetails(UserInfo userInfo, List<GrantedAuthority> grantedAuthorities) {
		Assert.notNull(userInfo, "userInfo cannot be null.");
		this.userInfo = userInfo;
		this.grantedAuthorities = grantedAuthorities;
		if (null == this.grantedAuthorities) {
			this.grantedAuthorities = new ArrayList<>();
		}
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public List<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}
	
}
