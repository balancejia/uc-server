package com.yealink.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.CheckParameter;
import com.yealink.model.GreatRolePermissionModel;
import com.yealink.model.RolePermissionModel;
import com.yealink.service.IRolePermissionService;
import com.yealink.uc.dao.PermissionMapper;
import com.yealink.uc.dao.RoleMapper;
import com.yealink.uc.dao.RolePermissionMapper;
import com.yealink.uc.dao.UserRoleMapper;
import com.yealink.uc.entity.Permission;
import com.yealink.uc.entity.PermissionExample;
import com.yealink.uc.entity.Role;
import com.yealink.uc.entity.RoleExample;
import com.yealink.uc.entity.RolePermission;
import com.yealink.uc.entity.RolePermissionExample;
import com.yealink.uc.entity.UserRole;
import com.yealink.uc.entity.UserRoleExample;

@Service
public class RolePermissionServiceImpl implements IRolePermissionService {

	@Autowired
	private RolePermissionMapper rolePermissionMapper;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@Autowired
	private ILocalCache<Long, JSONObject> rolePermissionCache;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Override
	public JSONArray insert(Long roleId, JSONArray jsonArray) {
		if (!isRoleExist(roleId)) {
			throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
		}

		if (!isPermissionExist(jsonArray)) {
			throw new UCException(ErrorCode.UC_PERMISSION_NOT_EXIST);
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			RolePermission rolePermission = new RolePermission();
			rolePermission.setRoleId(roleId);
			rolePermission.setPermissionId(jsonArray.getLong(i));
			try {
				rolePermissionMapper.insert(rolePermission);
			}catch (DuplicateKeyException e) {
				//ignore duplicate key
			}
			
		}
		// 更新缓存数据
		refreshRoleCache(roleId);
		return jsonArray;
	}

	@Override
	public List<RolePermissionModel> selectAllByRoleId(Long roleId, RolePermissionExample example) {
		if (!isRoleExist(roleId)) {
			throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
		}
		List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(example);
		List<RolePermissionModel> rolePermissionModels = new ArrayList<>();
		for (int i = 0; i < rolePermissions.size(); i++) {
			RolePermission rolePermission = rolePermissions.get(i);
			Permission permission =  permissionMapper.selectByPrimaryKey(rolePermission.getPermissionId());
			rolePermissionModels
					.add(new RolePermissionModel(rolePermission.getRoleId(), rolePermission.getPermissionId(),
							permission.getCode(),permission.getName()));
		}
		return rolePermissionModels;
	}

	@Override
	public JSONObject selectAllRolePermission(String permissionCode,String roleCode, Integer limit, Integer offset) {

		RolePermissionExample rolePermissionExample = new RolePermissionExample();
		RolePermissionExample.Criteria criteria = rolePermissionExample.createCriteria();
		rolePermissionExample.setLimit(limit);
		rolePermissionExample.setOffset(offset);
		if (!StringUtils.isEmpty(permissionCode)) {
			PermissionExample permissionExample = new PermissionExample();
			permissionExample.createCriteria().andCodeEqualTo(permissionCode);
			List<Permission> permissions = permissionMapper.selectByExample(permissionExample);
			if (permissions.isEmpty() || permissions.size() == 0) {
				throw new UCException(ErrorCode.UC_PERMISSION_NOT_EXIST);
			}
			Long permissionId = permissions.get(0).getId();
			criteria.andPermissionIdEqualTo(permissionId);
		}
		if (!StringUtils.isEmpty(roleCode)) {
			RoleExample roleExample = new RoleExample();
			roleExample.createCriteria().andCodeEqualTo(roleCode);
			List<Role> roles = roleMapper.selectByExample(roleExample);
			if (roles.isEmpty() || roles.size() == 0) {
				throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
			}
			Long roleId = roles.get(0).getId();
			criteria.andRoleIdEqualTo(roleId);
		}

		List<GreatRolePermissionModel> greatRolePermssionModels = new ArrayList<>();
		List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);
		Long total = rolePermissionMapper.countByExample(rolePermissionExample);
		for (int i = 0; i < rolePermissions.size(); i++) {
			Role role = roleMapper.selectByPrimaryKey(rolePermissions.get(i).getRoleId());
			Permission permission = permissionMapper.selectByPrimaryKey(rolePermissions.get(i).getPermissionId());
			greatRolePermssionModels.add(new GreatRolePermissionModel(role.getId(), permission.getId(), role.getCode(),
					role.getName(), permission.getCode(), permission.getName(), rolePermissions.get(i).getId()));
		}
		JSONObject result = new JSONObject();
		result.put("total", total);
		result.put("items", greatRolePermssionModels);
		return result;
	}

	@Override
	public Long countByExample(Long roleId) {
		if (!isRoleExist(roleId)) {
			throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
		}
		RolePermissionExample rolePermissionExample = new RolePermissionExample();
		rolePermissionExample.createCriteria().andRoleIdEqualTo(roleId);
		return rolePermissionMapper.countByExample(rolePermissionExample);
	}

	@Override
	public JSONArray deleteByExample(Long roleId, JSONArray jsonArray) {
		if (!isRoleExist(roleId)) {
			throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
		}
		RolePermissionExample example = new RolePermissionExample();
		for (int i = 0; i < jsonArray.size(); i++) {
			example.clear();
			example.createCriteria().andRoleIdEqualTo(roleId).andPermissionIdEqualTo(jsonArray.getLong(i));
			if (rolePermissionMapper.selectByExample(example).isEmpty()) {
				throw new UCException(ErrorCode.UC_PERMISSION_NOT_EXIST);
			}
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			example.clear();
			example.createCriteria().andRoleIdEqualTo(roleId).andPermissionIdEqualTo(jsonArray.getLong(i));
			rolePermissionMapper.deleteByExample(example);
		}

		// 更新缓存数据
		refreshRoleCache(roleId);
		return jsonArray;
	}

	@Override
	public JSONArray deleteByPrimaryKey(JSONArray rolePermissionJsonArray) {
		for (int i = 0; i < rolePermissionJsonArray.size(); i++) {
			if (rolePermissionMapper.selectByPrimaryKey(rolePermissionJsonArray.getLong(i)) == null) {
				throw new UCException(ErrorCode.UC_ROLE_PERMISSION_NOT_EXIST);
			}
		}
		for (int i = 0; i < rolePermissionJsonArray.size(); i++) {
			RolePermission rolePermission = rolePermissionMapper.selectByPrimaryKey(rolePermissionJsonArray.getLong(i));
			rolePermissionMapper.deleteByPrimaryKey(rolePermissionJsonArray.getLong(i));
			// 更新缓存数据
			refreshRoleCache(rolePermission.getRoleId());
		}
		return rolePermissionJsonArray;
	}

	public boolean isRoleExist(Long roleId) {
		if (roleMapper.selectByPrimaryKey(roleId) != null)
			return true;
		return false;
	}

	public boolean isPermissionExist(JSONArray jsonArray) {
		PermissionExample permissionExample = new PermissionExample();
		for (int i = 0; i < jsonArray.size(); i++) {
			permissionExample.clear();
			permissionExample.createCriteria().andIdEqualTo(jsonArray.getLong(i));
			if (permissionMapper.selectByExample(permissionExample).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void refreshRoleCache(Long roleId) {
		UserRoleExample userRoleExample = new UserRoleExample();
		userRoleExample.createCriteria().andRoleIdEqualTo(roleId);
		List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
		if (userRoles.size() > 0 && userRoles != null) {
			for (int i = 0; i < userRoles.size(); i++) {
				rolePermissionCache.refresh(userRoles.get(i).getUserId());
			}
		}
	}

}
