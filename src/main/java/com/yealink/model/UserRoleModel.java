package com.yealink.model;


public class UserRoleModel {
	
	private Long userId;
    private Long roleId;
    private String code;
    private String codeName;
    
    public UserRoleModel(Long userId, Long roleId, String code,String codeName) {
		this.userId = userId;
		this.roleId = roleId;
		this.code = code;
		this.codeName = codeName;
	}
    
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
    
    
}
