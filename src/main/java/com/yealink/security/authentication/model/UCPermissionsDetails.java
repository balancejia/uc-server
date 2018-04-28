package com.yealink.security.authentication.model;

import org.springframework.security.core.GrantedAuthority;


public class UCPermissionsDetails implements GrantedAuthority{
	
	private String permissionCode;
	
	public UCPermissionsDetails(String permissionCode){
		this.permissionCode = permissionCode;
	}
	
	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return this.getPermissionCode();
	}
	
}
