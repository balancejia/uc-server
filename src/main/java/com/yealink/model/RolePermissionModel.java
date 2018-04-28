package com.yealink.model;

public class RolePermissionModel {
	private Long permissionId;
    private Long roleId;
    private String code;
    private String permissionName;
    
    public RolePermissionModel(Long roleId, Long permissionId, String code, String permissionName) {
		this.roleId = roleId;
		this.permissionId = permissionId;
		this.code = code;
		this.permissionName = permissionName;
	}
    
	public Long getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
    
    
    
}
