package com.yealink.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.service.IRoleService;
import com.yealink.uc.dao.RoleMapper;
import com.yealink.uc.dao.RolePermissionMapper;
import com.yealink.uc.dao.UserRoleMapper;
import com.yealink.uc.entity.Role;
import com.yealink.uc.entity.RoleExample;
import com.yealink.uc.entity.RolePermissionExample;
import com.yealink.uc.entity.UserRole;
import com.yealink.uc.entity.UserRoleExample;

@Service
public class RoleServiceImpl extends CommonServiceImpl<Role, RoleExample> implements IRoleService {

	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private ILocalCache<Long, JSONObject> rolePermissionCache;
	
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	
	@Override
	public int insert(Role record) {
		try {
			return roleMapper.insert(record);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_ROLE_EXISTED);
		}
	}

	@Override
	public long countByExample(RoleExample example) {
		return roleMapper.countByExample(example);
	}

	@Override
	public List<Role> selectByExample(RoleExample example) {
		return roleMapper.selectByExample(example);
	}

	@Override
	public int updateByExampleSelective(Role record, Long id) {
		RoleExample roleExample = new RoleExample();
		roleExample.createCriteria().andIdEqualTo(id);
		try{
			int num = roleMapper.updateByExampleSelective(record, roleExample);
			//更新缓存
			UserRoleExample userRoleExample = new UserRoleExample();
			userRoleExample.createCriteria().andRoleIdEqualTo(id);
			List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
			refreshRoleCache(userRoles);
			return num;
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_ROLE_EXISTED);
		}
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		selectByPrimaryKey(id);
		//删除用户角色
		UserRoleExample userRoleExample = new UserRoleExample();
		userRoleExample.createCriteria().andRoleIdEqualTo(id);
		List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
		userRoleMapper.deleteByExample(userRoleExample);
		//删除角色权限
		RolePermissionExample rolePermissionExample = new RolePermissionExample();
		rolePermissionExample.createCriteria().andRoleIdEqualTo(id);
		rolePermissionMapper.deleteByExample(rolePermissionExample);
		//删除角色
		int num = roleMapper.deleteByPrimaryKey(id);
		//刷新缓存
		refreshRoleCache(userRoles);
		return num;
	}
	
	@Override
	public Role selectByPrimaryKey(Long id) {
		Role role = roleMapper.selectByPrimaryKey(id);
		if(role != null){
			return role;
		}
		throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
	}

	@Override
	public JSONObject findAllByExample(RoleExample example) {
		JSONObject result = new JSONObject();
		List<Role> roles = selectByExample(example);
		Long total = countByExample(example);
		result.put("total", total);
		result.put("items", roles);
		return result;
	}
	
	public void refreshRoleCache(List<UserRole> userRoles){
		if(userRoles.size() > 0 && userRoles != null){
			for(int i = 0; i < userRoles.size(); i++){
				rolePermissionCache.refresh(userRoles.get(i).getUserId());
			}
		}
	}
}
