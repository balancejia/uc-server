package com.yealink.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.alibaba.druid.util.StringUtils;
import com.yealink.uc.entity.Permission;

public class UpdatePermissionForm {
	@Size(max=100, message=("uc.permission.code.invalid"))
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
    
    public static Permission toPermission(UpdatePermissionForm updatePermissionForm, Permission permission){
    	if (!StringUtils.isEmpty(updatePermissionForm.getCode())) {
    		permission.setCode(updatePermissionForm.getCode());
		}
    	if (!StringUtils.isEmpty(updatePermissionForm.getName())) {
    		permission.setName(updatePermissionForm.getName());
		}
    	return permission;
    }
}
