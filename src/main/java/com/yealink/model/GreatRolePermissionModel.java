package com.yealink.model;

public class GreatRolePermissionModel {

	private Long permissionId;
	private Long roleId;
	private String roleCode;
	private String roleName;
	private String permissionCode;
	private String permissionName;
	private Long id;

	public GreatRolePermissionModel(Long roleId, Long permissionId, String roleCode, String roleName,
			String permissionCode, String permissionName, Long id) {
		this.roleId = roleId;
		this.permissionId = permissionId;
		this.roleCode = roleCode;
		this.roleName = roleName;
		this.permissionCode = permissionCode;
		this.permissionName = permissionName;
		this.id = id;
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

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

}
