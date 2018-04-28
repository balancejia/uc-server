package com.yealink.security.authentication.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户信息
 *
 * Created by WUWY(188895) on 2016/5/23.
 */
public class UserInfo implements Serializable {
	/**
	 * 用户的编号
	 */
	private String userId;
	/**
	 * 用户的名称
	 */
	private String userName;
	/**
	 * 用户的邮箱
	 */
	private String email;
	/**
	 * 用户的状态
	 */
	private String status;
	/**
	 * 用户的昵称
	 */
	private String nickName;
	/**
	 * 用户领域扩展属性
	 */
	private Map<String, Object> realmExinfo;
	/**
	 * 用户组织扩展属性
	 */

	private String userType;// 请求类型，"Mac" or "Bearer"
	
	private String source;
	
	private String realm;

	private Map<String, Object> orgExinfo;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Map<String, Object> getRealmExinfo() {
		return realmExinfo;
	}

	public void setRealmExinfo(Map<String, Object> realmExinfo) {
		this.realmExinfo = realmExinfo;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Map<String, Object> getOrgExinfo() {
		return orgExinfo;
	}

	public void setOrgExinfo(Map<String, Object> orgExinfo) {
		this.orgExinfo = orgExinfo;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	
}
