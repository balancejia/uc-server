package com.yealink.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.yealink.model.GreatGroupPermissionModel;
import com.yealink.model.GroupPermissionModel;
import com.yealink.service.IGroupPermissionService;
import com.yealink.uc.dao.PermissionMapper;
import com.yealink.uc.dao.UserGroupMapper;
import com.yealink.uc.dao.UserGroupPermissionMapper;
import com.yealink.uc.dao.UserMapper;
import com.yealink.uc.entity.Permission;
import com.yealink.uc.entity.PermissionExample;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserExample;
import com.yealink.uc.entity.UserGroup;
import com.yealink.uc.entity.UserGroupExample;
import com.yealink.uc.entity.UserGroupPermission;
import com.yealink.uc.entity.UserGroupPermissionExample;

@Service
public class GroupPermissionServiceImpl implements IGroupPermissionService{

	@Autowired
	private UserGroupPermissionMapper groupPermissionMapper;
	
	@Autowired
	private UserGroupMapper groupMapper;
	
	@Autowired
	private PermissionMapper permissionMapper;
	
	@Autowired
	private ILocalCache<Long, List<String>> groupPermissionCache;

	
	@Override
	public JSONArray insert(Long groupId, JSONArray jsonArray) {
		if (!isGroupExist(groupId)) {
			throw new UCException(ErrorCode.AD_GROUP_NOT_EXIST);
		}

		if (!isPermissionExist(jsonArray)) {
			throw new UCException(ErrorCode.UC_PERMISSION_NOT_EXIST);
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			UserGroupPermission groupPermission = new UserGroupPermission();
			groupPermission.setGroupId(groupId);
			groupPermission.setPermissionId(jsonArray.getLong(i));
			try {
				groupPermissionMapper.insert(groupPermission);
			}catch (DuplicateKeyException e) {
				//ignore duplicate key
			}
			
		}
		// 更新缓存数据
		groupPermissionCache.refresh(groupId);
		return jsonArray;
	}

	@Override
	public List<GroupPermissionModel> selectAllByGroupId(Long groupId, UserGroupPermissionExample example) {
		if (!isGroupExist(groupId)) {
			throw new UCException(ErrorCode.AD_GROUP_NOT_EXIST);
		}
		List<UserGroupPermission> groupPermissions = groupPermissionMapper.selectByExample(example);
		List<GroupPermissionModel> groupPermissionModels = new ArrayList<>();
		for (int i = 0; i < groupPermissions.size(); i++) {
			UserGroupPermission groupPermission = groupPermissions.get(i);
			Permission permission = permissionMapper.selectByPrimaryKey(groupPermission.getPermissionId());
			groupPermissionModels
					.add(new GroupPermissionModel(groupId, groupPermission.getPermissionId(),
							permission.getCode(),permission.getName()));
		}
		return groupPermissionModels;
	}

	@Override
	public Long countByExample(Long groupId) {
		if (!isGroupExist(groupId)) {
			throw new UCException(ErrorCode.AD_GROUP_NOT_EXIST);
		}
		UserGroupPermissionExample groupPermissionExample = new UserGroupPermissionExample();
		groupPermissionExample.createCriteria().andGroupIdEqualTo(groupId);
		return groupPermissionMapper.countByExample(groupPermissionExample);
	}

	@Override
	public JSONArray deleteByExample(Long groupId, JSONArray jsonArray) {
		if (!isGroupExist(groupId)) {
			throw new UCException(ErrorCode.AD_GROUP_NOT_EXIST);
		}
		UserGroupPermissionExample example = new UserGroupPermissionExample();
		for (int i = 0; i < jsonArray.size(); i++) {
			example.clear();
			example.createCriteria().andGroupIdEqualTo(groupId).andPermissionIdEqualTo(jsonArray.getLong(i));
			if (groupPermissionMapper.selectByExample(example).isEmpty()) {
				throw new UCException(ErrorCode.UC_PERMISSION_NOT_EXIST);
			}
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			example.clear();
			example.createCriteria().andGroupIdEqualTo(groupId).andPermissionIdEqualTo(jsonArray.getLong(i));
			groupPermissionMapper.deleteByExample(example);
		}

		// 更新缓存数据
		groupPermissionCache.refresh(groupId);
		return jsonArray;
	}

	@Override
	public JSONObject selectAllGroupPermission(String permissionCode,String groupCode, Integer limit, Integer offset) {

		UserGroupPermissionExample groupPermissionExample = new UserGroupPermissionExample();
		UserGroupPermissionExample.Criteria criteria = groupPermissionExample.createCriteria();
		groupPermissionExample.setLimit(limit);
		groupPermissionExample.setOffset(offset);
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
		if (!StringUtils.isEmpty(groupCode)) {
			UserGroupExample groupExample = new UserGroupExample();
			groupExample.createCriteria().andCodeEqualTo(groupCode);
			List<UserGroup> groups = groupMapper.selectByExample(groupExample);
			if (groups.isEmpty() || groups.size() == 0) {
				throw new UCException(ErrorCode.AD_GROUP_NOT_EXIST);
			}
			Long groupId = groups.get(0).getId();
			criteria.andGroupIdEqualTo(groupId);
		}

		List<GreatGroupPermissionModel> greatGroupPermssionModels = new ArrayList<>();
		List<UserGroupPermission> groupPermissions = groupPermissionMapper.selectByExample(groupPermissionExample);
		Long total = groupPermissionMapper.countByExample(groupPermissionExample);
		for (int i = 0; i < groupPermissions.size(); i++) {
			UserGroup group = groupMapper.selectByPrimaryKey(groupPermissions.get(i).getGroupId());
			Permission permission = permissionMapper.selectByPrimaryKey(groupPermissions.get(i).getPermissionId());
			greatGroupPermssionModels.add(new GreatGroupPermissionModel(group.getId(), permission.getId(), group.getCode(),
					group.getName(), permission.getCode(), permission.getName(), groupPermissions.get(i).getId()));
		}
		JSONObject result = new JSONObject();
		result.put("total", total);
		result.put("items", greatGroupPermssionModels);
		return result;
	}

	@Override
	public JSONArray deleteByPrimaryKey(JSONArray groupPermissionJsonArray) {
		for (int i = 0; i < groupPermissionJsonArray.size(); i++) {
			if (groupPermissionMapper.selectByPrimaryKey(groupPermissionJsonArray.getLong(i)) == null) {
				throw new UCException(ErrorCode.AD_GROUP_PERMISSION_NOT_EXIST);
			}
		}
		for (int i = 0; i < groupPermissionJsonArray.size(); i++) {
			UserGroupPermission groupPermission = groupPermissionMapper.selectByPrimaryKey(groupPermissionJsonArray.getLong(i));
			groupPermissionMapper.deleteByPrimaryKey(groupPermissionJsonArray.getLong(i));
			// 更新缓存数据
			groupPermissionCache.refresh(groupPermission.getGroupId());
		}
		return groupPermissionJsonArray;
	}
	
	public boolean isGroupExist(Long groupId) {
		if (groupMapper.selectByPrimaryKey(groupId) != null){
			return true;	
		}
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

}
