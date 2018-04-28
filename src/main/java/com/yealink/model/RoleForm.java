package com.yealink.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.alibaba.druid.util.StringUtils;
import com.yealink.uc.entity.Role;

public class RoleForm {
	@Size(max=100, message=("uc.role.code.invalid"))
	@Pattern(regexp="^ROLE_[A-Z_]*", message=("uc.role.code.invalid"))
	@NotNull(message=("uc.role.code.invalid"))
	private String code;

	/**
	 * 角色名称
	 */
	@Size(max=100, message=("uc.role.name.invalid"))
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static Role toRole(RoleForm roleForm, Role role){
		role.setCode(roleForm.getCode());
		if(!StringUtils.isEmpty(roleForm.getName())){
			role.setName(roleForm.getName());
		}
		return role;
	}
}
