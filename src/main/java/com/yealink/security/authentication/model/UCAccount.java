package com.yealink.security.authentication.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * UCAccount UC账号对象
 *
 */
public class UCAccount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6186346038778542610L;
	@NotNull(message = "uc.token.username.invalid")
	private String username;
	@NotNull(message = "uc.token.password.invalid")
	@Size(min=6, max=50, message = "uc.token.password.invalid")
	private String password;
	private String userId;
	private String code;
	public UCAccount() {}
	public UCAccount(String username, String password, String code) {
		Assert.notNull(username, "username cannot be null.");
		Assert.notNull(password, "password cannot be null.");
		this.username = username;
		this.password = password;
		this.code = code;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	

}
