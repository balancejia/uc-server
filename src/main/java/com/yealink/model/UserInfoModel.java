package com.yealink.model;

import java.util.Date;

public class UserInfoModel {
	 /**
     * 用户编号
     */
    private Long id;

    /**
     * 用户名
     */

    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户状态(0启用,1禁用)
     */
    private String status;

    /**
     * 用户头像
     */
    private String avatar;

    private Date createTime;

    private Date updateTime;
    
    private String realm;
    
    


	public UserInfoModel(Long id, String username, String nickname, String email, String status, String avatar,
			Date createTime, Date updateTime, String realm) {
		super();
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.email = email;
		this.status = status;
		this.avatar = avatar;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.realm = realm;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

    
    
}
