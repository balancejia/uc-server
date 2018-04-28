package com.yealink.security.authentication.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.yealink.security.authentication.model.UCMacToken;
import com.yealink.security.authentication.model.UCPermissionsDetails;
import com.yealink.security.authentication.model.UCRoleDetails;
import com.yealink.security.authentication.model.UCUserDetails;
import com.yealink.security.authentication.model.UserInfo;
import com.yealink.security.authentication.service.IUserDetailsService;
import com.yealink.service.impl.UserServiceImpl;
import com.yealink.uc.entity.User;

/**
 * 用户详细信息的处理服务的实现类。
 */
public class UCUserDetailsService implements IUserDetailsService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Override
	public UCUserDetails loadUserDetailsByUserId(String userId) {
		// 获取用户信息,包括用户权限
		UserInfo userInfo = this.getUserInfo(userId);
		Assert.notNull(userInfo, "userInfo cannot be null.");
		logger.debug("user:{}, userId:{} ,realm:{}", userInfo, userId);
		
		List<UCPermissionsDetails> permissions =  this.getUserPermissionList(userId);
		List<UCRoleDetails> roles = this.getUserRoleList(userId);
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for(UCPermissionsDetails permissionCode : permissions){
			if(permissionCode != null && permissionCode.getPermissionCode() != null){
				GrantedAuthority grantedAuthority = new UCPermissionsDetails(permissionCode.getPermissionCode());
				grantedAuthorities.add(grantedAuthority);
			}
		}
		for(UCRoleDetails roleCode : roles){
			if(roleCode != null && roleCode.getRoleCode() != null){
				GrantedAuthority grantedAuthority = new UCRoleDetails(roleCode.getRoleCode());
				grantedAuthorities.add(grantedAuthority);
			}
		}
		return new UCUserDetails(userInfo, grantedAuthorities);
	}

	@Override
	public UserInfo getUserInfo(String userId) {
		Assert.notNull(userId, "userId cannot be null.");
		UserInfo userInfo = new UserInfo();
		User user = userServiceImpl.selectByPrimaryKey(Long.valueOf(userId));
		userInfo.setUserId(userId);
		userInfo.setNickName(user.getNickname());
		userInfo.setUserName(user.getUsername());
		userInfo.setEmail(user.getEmail());
		userInfo.setStatus(user.getStatus());
		userInfo.setRealm(user.getRealm());
		userInfo.setSource(user.getSource());
		return userInfo;
	}

	@Override
	public List<UCPermissionsDetails> getUserPermissionList(String userId) {
		Assert.notNull(userId, "userId cannot be null.");
		JSONObject rolePermissions = userServiceImpl.getRolePermission(Long.valueOf(userId));
		Set<String> permissions = (Set<String>) rolePermissions.get("permissions");
		List<UCPermissionsDetails> ucPermissionsDetails = new ArrayList<>();
		for(String permission : permissions){
			if(!StringUtils.isEmpty(permission)){
				ucPermissionsDetails.add(new UCPermissionsDetails(permission));
			}
		}
		return ucPermissionsDetails;
	}

	@Override
	public List<UCRoleDetails> getUserRoleList(String userId) {
		Assert.notNull(userId, "userId cannot be null.");
		JSONObject rolePermissions = userServiceImpl.getRolePermission(Long.valueOf(userId));
		List<String> roleCodes = (List<String>) rolePermissions.get("roles");
		List<UCRoleDetails> ucRoleDetails = new ArrayList<>();
		for(String rolecode : roleCodes){
			if(!StringUtils.isEmpty(rolecode)){
				ucRoleDetails.add(new UCRoleDetails(rolecode));
			}
		}
		return ucRoleDetails;
	}
	
	@Override
	public UCUserDetails loadUserDetailsByUserId(UCMacToken safUcToken) {
		String userId = safUcToken.getUserId();
		Assert.notNull(userId, "userId cannot be null.");
		return this.loadUserDetailsByUserId(userId);
	}

}
