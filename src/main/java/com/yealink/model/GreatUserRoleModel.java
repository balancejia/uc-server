package com.yealink.model;

public class GreatUserRoleModel {

	private Long id;
	private Long userId;
	private Long roleId;
	private String code;
	private String username;
	private String nickname;

	public GreatUserRoleModel(Long userId, Long roleId, String code, String username,Long id, String nickname) {
		this.userId = userId;
		this.roleId = roleId;
		this.code = code;
		this.username = username;
		this.id = id;
		this.nickname = nickname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	

}
