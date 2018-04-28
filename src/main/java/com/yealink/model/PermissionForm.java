package com.yealink.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.alibaba.druid.util.StringUtils;
import com.yealink.uc.entity.Permission;

public class PermissionForm {

	@Size(max=100, message=("uc.permission.code.invalid"))
    @NotNull(message=("uc.permission.code.invalid"))
	@Pattern(regexp="^P_.*", message=("uc.permission.code.invalid"))
    private String code;

    @Size(max=100, message=("uc.permission.name.invalid"))
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
    
    public static Permission toPermission(PermissionForm permissionForm, Permission permission){
    	permission.setCode(permissionForm.getCode());
    	if (!StringUtils.isEmpty(permissionForm.getName())) {
    		permission.setName(permissionForm.getName());
		}
    	return permission;
    }
}
