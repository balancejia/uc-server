package com.yealink.cache.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.cache.guava.GuavaAbstractLoadingCache;
import com.yealink.cache.local.ILocalCache;
import com.yealink.model.GreatGroupPermissionModel;
import com.yealink.model.GroupPermissionModel;
import com.yealink.model.RolePermissionModel;
import com.yealink.model.UserRoleModel;
import com.yealink.service.impl.GroupPermissionServiceImpl;
import com.yealink.service.impl.RolePermissionServiceImpl;
import com.yealink.service.impl.UserRoleServiceImpl;
import com.yealink.service.impl.UserServiceImpl;
import com.yealink.uc.dao.UserGroupMapper;
import com.yealink.uc.dao.UserMapper;
import com.yealink.uc.entity.RolePermissionExample;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserGroup;
import com.yealink.uc.entity.UserGroupExample;
import com.yealink.uc.entity.UserGroupPermissionExample;
import com.yealink.uc.entity.UserRoleExample;

@Component
public class RolePermissionCacheImpl extends GuavaAbstractLoadingCache<Long, JSONObject> implements ILocalCache<Long, JSONObject>{
	
	private RolePermissionCacheImpl() {
		setMaximumSize(1000);
		setExpireAfterWriteDuration(1800);
	}
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserRoleServiceImpl userRoleServiceImpl;
	
	@Autowired
	RolePermissionServiceImpl rolePermissionServiceImpl;
	
	@Autowired
	UserGroupMapper userGroupMapper;
	
	
	@Override
	public JSONObject get(Long key) {
		try {
			return getValue(key);
		} catch (ExecutionException e) {
			logger.error("Unable to get cache data",key,e);
			return null;
		}
	}

	@Override
	public void refresh(Long key) {
		getCache().refresh(key);		
	}
	
	@Override
	public void put(Long key, JSONObject value) {
		getCache().put(key, value);
	}
	
	@Override
	public void invalidate(Long key) {
		getCache().invalidate(key);
	}

	@Override
	public void invalidateAll() {
		getCache().invalidateAll();
		
	}

	@Override
	public ConcurrentMap<Long, JSONObject> asMap() {
		return getCache().asMap();
	}
	
	@Override
	public JSONObject getIfPresent(Long key) {
		return getCache().getIfPresent(key);
	}
	/*
	 * 从数据库获取数据
	 */
	@Override
	protected JSONObject fetchData(Long userId) {
		JSONObject result = new JSONObject();
		UserRoleExample userRoleExample = new UserRoleExample(); 
		userRoleExample.createCriteria().andUserIdEqualTo(userId);
		List<UserRoleModel> userRole = userRoleServiceImpl.selectAllByUserId(userId, userRoleExample);
		List<String> roleCode = new ArrayList<>();
		for(int i = 0; i < userRole.size(); i++){
			roleCode.add(userRole.get(i).getCode());
		}
		Set<String> permissionCode = new HashSet<>();
		RolePermissionExample rolePermissionExample = new RolePermissionExample();
		for(int i = 0; i < userRole.size(); i++){
			rolePermissionExample.clear();
			rolePermissionExample.createCriteria().andRoleIdEqualTo(userRole.get(i).getRoleId());
			List<RolePermissionModel> rolePermissions = rolePermissionServiceImpl.selectAllByRoleId(userRole.get(i).getRoleId(), rolePermissionExample);
			for(int j = 0; j < rolePermissions.size(); j++){
				permissionCode.add(rolePermissions.get(j).getCode());
			}
		}
		result.put("roles", roleCode);
		result.put("permissions", permissionCode);
		return result;
	}
	

}
