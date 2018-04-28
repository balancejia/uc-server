package com.yealink.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.alibaba.druid.util.StringUtils;
import com.yealink.uc.entity.User;

public class UpdateUserForm {

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
    private String email;

   

    /**
     * 用户状态(0启用,1禁用)
     */
    @Pattern(regexp="0|1", message=("uc.user.status.invalid"))
    private String status;

    /**
     * 用户头像
     */
    private String avatar;


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
    
	public static User toUser(UpdateUserForm updateUserForm, User user){
		if(!StringUtils.isEmpty(updateUserForm.getEmail())){
			user.setEmail(updateUserForm.getEmail());
		}
		if(!StringUtils.isEmpty(updateUserForm.getNickname())){
			user.setNickname(updateUserForm.getNickname());
		}
		if(!StringUtils.isEmpty(updateUserForm.getStatus())){
			user.setStatus(updateUserForm.getStatus());
		}
		if(!StringUtils.isEmpty(updateUserForm.getAvatar())){
			user.setAvatar(updateUserForm.getAvatar());
		}
		return user;
	}
}
