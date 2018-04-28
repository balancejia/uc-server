package com.yealink.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PasswordForm {

	@NotNull(message = "uc.user.password.invalid")
	@Size(min=6, max=50, message = "uc.user.password.invalid")
    private String newPassword;
	
	private String oldPassword;

	private String verifyPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}

	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	

	
}
