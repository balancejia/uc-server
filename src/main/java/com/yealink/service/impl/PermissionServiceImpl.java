package com.yealink.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.uc.dao.PermissionMapper;
import com.yealink.uc.dao.RolePermissionMapper;
import com.yealink.uc.dao.UserGroupPermissionMapper;
import com.yealink.uc.dao.UserRoleMapper;
import com.yealink.uc.entity.Permission;
import com.yealink.uc.entity.PermissionExample;
import com.yealink.uc.entity.RolePermission;
import com.yealink.uc.entity.RolePermissionExample;
import com.yealink.uc.entity.UserGroupPermission;
import com.yealink.uc.entity.UserGroupPermissionExample;
import com.yealink.uc.entity.UserRole;
import com.yealink.uc.entity.UserRoleExample;

@Service
public class PermissionServiceImpl extends CommonServiceImpl<Permission, PermissionExample> {

	@Autowired
	private PermissionMapper permissionMapper;
	
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private ILocalCache<Long, JSONObject> rolePermissionCache;
	
	@Autowired
	private ILocalCache<Long, List<String>> groupPermissionCache;
	
	@Autowired
	private UserGroupPermissionMapper groupPermissionMapper;
	
	@Override
	public long countByExample(PermissionExample example) {
		return permissionMapper.countByExample(example);
	}

	@Override
	public int insert(Permission record) {
		try {
			return permissionMapper.insert(record);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_PERMISSION_EXISTED);
		}
	}

	@Override
	public Permission selectByPrimaryKey(Long id) {
		Permission permission = permissionMapper.selectByPrimaryKey(id);
		if(permission != null){
			return permission;
		}
		throw new UCException(ErrorCode.UC_PERMISSION_NOT_EXIST);
	}

	@Override
	public List<Permission> selectByExample(PermissionExample example) {
		return permissionMapper.selectByExample(example);
	}

	@Override
	public int updateByExampleSelective(Permission record, Long id) {
		PermissionExample permissionExample = new PermissionExample();
		permissionExample.createCriteria().andIdEqualTo(id);
		try {
			int num = permissionMapper.updateByExampleSelective(record, permissionExample);
			//更新缓存
			RolePermissionExample rolePermissionExample = new RolePermissionExample();
			rolePermissionExample.createCriteria().andPermissionIdEqualTo(id);
			List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);
			refreshPermissionCache(rolePermissions);
			return num;
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_PERMISSION_EXISTED);
		}
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		selectByPrimaryKey(id);
		//删除角色权限
		RolePermissionExample rolePermissionExample = new RolePermissionExample();
		rolePermissionExample.createCriteria().andPermissionIdEqualTo(id);
		List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);
		rolePermissionMapper.deleteByExample(rolePermissionExample);
		//删除用户组权限
		UserGroupPermissionExample userGroupPermissionExample = new UserGroupPermissionExample();
		userGroupPermissionExample.createCriteria().andPermissionIdEqualTo(id);
		List<UserGroupPermission> groupPermissions = groupPermissionMapper.selectByExample(userGroupPermissionExample);
		groupPermissionMapper.deleteByExample(userGroupPermissionExample);
		//删除权限
		int num = permissionMapper.deleteByPrimaryKey(id);
		//删除缓存的数据
		refreshPermissionCache(rolePermissions);
		refreshGroupPermissionCache(groupPermissions);
		
		return num;
	}


	@Override
	public JSONObject findAllByExample(PermissionExample example) {
		JSONObject result = new JSONObject();
		List<Permission> permissions = selectByExample(example);
		Long total = countByExample(example);
		result.put("total", total);
		result.put("items", permissions);
		return result;
	}
	
	public void refreshPermissionCache(List<RolePermission> rolePermissions){
		for(int i = 0; i < rolePermissions.size(); i++){
			UserRoleExample userRoleExample = new UserRoleExample();
			userRoleExample.createCriteria().andRoleIdEqualTo(rolePermissions.get(i).getRoleId());
			List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
			if(userRoles.size() > 0 && userRoles != null){
				for(int j = 0; j < userRoles.size(); j++){
					rolePermissionCache.refresh(userRoles.get(j).getUserId());
				}
			}
			
		}
	}
	public void refreshGroupPermissionCache(List<UserGroupPermission> groupPermissions){
		for(int i = 0; i < groupPermissions.size(); i++){
			groupPermissionCache.refresh(groupPermissions.get(i).getGroupId());
		}
	}
}
