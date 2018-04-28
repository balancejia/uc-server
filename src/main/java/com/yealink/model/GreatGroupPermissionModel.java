package com.yealink.model;

public class GreatGroupPermissionModel {

	private Long permissionId;
	private Long groupId;
	private String groupCode;
	private String groupName;
	private String permissionCode;
	private String permissionName;
	private Long id;
	public GreatGroupPermissionModel(Long permissionId, Long groupId, String groupCode, String groupName,
			String permissionCode, String permissionName, Long id) {
		super();
		this.permissionId = permissionId;
		this.groupId = groupId;
		this.groupCode = groupCode;
		this.groupName = groupName;
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
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getPermissionCode() {
		return permissionCode;
	}
	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
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
