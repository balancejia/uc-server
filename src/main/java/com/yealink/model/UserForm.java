package com.yealink.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.alibaba.druid.util.StringUtils;
import com.yealink.uc.entity.User;

public class UserForm{

    /**
     * 用户名
     */
    @NotNull(message = "uc.user.username.invalid")
    @Size(max=50, min=6,  message = "uc.user.username.invalid")
    @Email(message = "uc.user.username.invalid")
    private String username;

    /**
     * 密码
     */
	@NotNull(message = "uc.user.password.invalid")
	@Size(min=6, max=50, message = "uc.user.password.invalid")
    private String password;


    /**
     * 昵称
     */
    @Size(max=50, message = "uc.user.nickname.invalid")
    private String nickname;

    /**
     * 邮箱地址
     */
    @Email(message = "uc.user.email.invalid")
    @Size(max=50, message = "uc.user.email.invalid")
    @NotNull(message = "uc.user.email.invalid")
    private String email;

   

    /**
     * 用户状态(0启用,1禁用)
     */
    @Pattern(regexp="0|1", message="uc.user.status.invalid")
    @NotNull(message="uc.user.status.invalid")
    private String status;
    
    /**
     * 用户头像
     */
    private String avatar;
    
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public static User toUser(UserForm userForm, User user){
		user.setUsername(userForm.getUsername());
		user.setEmail(userForm.getEmail());
		user.setPassword(userForm.getPassword());
		user.setStatus(userForm.getStatus());

		if(!StringUtils.isEmpty(userForm.getNickname())){
			user.setNickname(userForm.getNickname());
		}
		if(!StringUtils.isEmpty(userForm.getAvatar())){
			user.setAvatar(userForm.getAvatar());
		}
		return user;
	}

	
}
