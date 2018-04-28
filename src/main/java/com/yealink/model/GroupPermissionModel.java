package com.yealink.model;

public class GroupPermissionModel {
	private Long permissionId;
    private Long groupId;
    private String code;
    private String permissionName;
	public GroupPermissionModel( Long groupId,Long permissionId, String code,String permissionName) {
		super();
		this.permissionId = permissionId;
		this.groupId = groupId;
		this.code = code;
		this.permissionName = permissionName;
	}
	public Long getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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
