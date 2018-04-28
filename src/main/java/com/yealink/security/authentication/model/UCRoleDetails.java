package com.yealink.security.authentication.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;

/**
 * 扩展自GrantAuthority对象，除了获取角色外，封装UC返回的角色相关的信息内容
 *
 */
public class UCRoleDetails implements GrantedAuthority{

	private String roleCode;
	
	public UCRoleDetails(String roleCode) {
		this.setRoleCode(roleCode);
	}
	
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getRoleCode() {
		return roleCode;
	}

	@Override
	public String getAuthority() {
		return this.getRoleCode();
	}
	
}
