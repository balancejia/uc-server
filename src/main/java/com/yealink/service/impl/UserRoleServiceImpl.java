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
import com.yealink.model.GreatUserRoleModel;
import com.yealink.model.UserRoleModel;
import com.yealink.service.IUserRoleService;
import com.yealink.uc.dao.RoleMapper;
import com.yealink.uc.dao.UserMapper;
import com.yealink.uc.dao.UserRoleMapper;
import com.yealink.uc.entity.Role;
import com.yealink.uc.entity.RoleExample;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserExample;
import com.yealink.uc.entity.UserRole;
import com.yealink.uc.entity.UserRoleExample;

@Service
public class UserRoleServiceImpl implements IUserRoleService {

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private ILocalCache<Long, JSONObject> rolePermissionCache;

	@Override
	public JSONArray insert(Long userId, JSONArray jsonArray) {
		if (!isUserExist(userId)) {
			throw new UCException(ErrorCode.UC_USERNAME_NOT_EXIST);
		}

		if (!isRoleExist(jsonArray)) {
			throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			UserRole userRole = new UserRole();
			userRole.setUserId(userId);
			userRole.setRoleId(jsonArray.getLong(i));
			try {
				userRoleMapper.insert(userRole);
			} catch (DuplicateKeyException e) {
				// ignore duplicate key
			}
		}
		// 更新缓存内的数据
		rolePermissionCache.refresh(userId);
		return jsonArray;
	}

	@Override
	public Long countByExample(Long userId) {
		if (!isUserExist(userId)) {
			throw new UCException(ErrorCode.UC_USERNAME_NOT_EXIST);
		}
		UserRoleExample example = new UserRoleExample();
		example.createCriteria().andUserIdEqualTo(userId);
		return userRoleMapper.countByExample(example);
	}

	@Override
	public List<UserRoleModel> selectAllByUserId(Long userId, UserRoleExample example) {
		if (!isUserExist(userId)) {
			throw new UCException(ErrorCode.UC_USERNAME_NOT_EXIST);
		}
		List<UserRoleModel> userRoleModels = new ArrayList<>();
		List<UserRole> userRoles = userRoleMapper.selectByExample(example);
		for (int i = 0; i < userRoles.size(); i++) {
			UserRole userRole = userRoles.get(i);
			Role role = roleMapper.selectByPrimaryKey(userRole.getRoleId());
			userRoleModels.add(new UserRoleModel(userRole.getUserId(), userRole.getRoleId(),
					role.getCode(),role.getName()));
		}
		return userRoleModels;
	}

	@Override
	public JSONObject selectAllUserRole(String keyword,String roleCode, Integer limit, Integer offset) {
		UserRoleExample userRoleExample = new UserRoleExample();
		UserRoleExample.Criteria criteria = userRoleExample.createCriteria();
		userRoleExample.setLimit(limit);
		userRoleExample.setOffset(offset);
		List<User> users = new ArrayList<>();
		if (!StringUtils.isEmpty(keyword)) {
			UserExample userExample = new UserExample();
			userExample.createCriteria().andUsernameLike("%" + CheckParameter.checkSql(keyword) + "%");
			UserExample.Criteria criteriaKeyword = userExample.createCriteria();
			criteriaKeyword.andNicknameLike("%" + CheckParameter.checkSql(keyword) + "%");
			userExample.or(criteriaKeyword);
			users = userMapper.selectByExample(userExample);
			if (users.isEmpty() || users.size() == 0) {
				throw new UCException(ErrorCode.UC_USERNAME_NOT_EXIST);
			}
		}
		
		Long roleId = null;
		if (!StringUtils.isEmpty(roleCode)) {
			RoleExample roleExample = new RoleExample();
			roleExample.createCriteria().andCodeEqualTo(roleCode);
			List<Role> roles = roleMapper.selectByExample(roleExample);
			if (roles.isEmpty() || roles.size() == 0) {
				throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
			}
			roleId = roles.get(0).getId();
			criteria.andRoleIdEqualTo(roleId);
		}
		
		if(!users.isEmpty() && users.size() > 0){
			criteria.andUserIdEqualTo(users.get(0).getId());
			for(int i = 1; i < users.size(); i++){
				UserRoleExample.Criteria criteriaUserId = userRoleExample.createCriteria();
				criteriaUserId.andUserIdEqualTo(users.get(i).getId());
				if(roleId!=null){
					criteriaUserId.andRoleIdEqualTo(roleId);
				}
				userRoleExample.or(criteriaUserId);
			}
		}
		
		JSONObject result = new JSONObject();
		List<GreatUserRoleModel> greatUserRoleModels = new ArrayList<>();
		List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
		for (int i = 0; i < userRoles.size(); i++) {
			Role role = roleMapper.selectByPrimaryKey(userRoles.get(i).getRoleId());
			User user = userMapper.selectByPrimaryKey(userRoles.get(i).getUserId());
			if(user == null){
				UserRoleExample example = new UserRoleExample();
				example.createCriteria().andUserIdEqualTo(userRoles.get(i).getUserId());
				userRoleMapper.deleteByExample(example);
			}
			greatUserRoleModels.add(new GreatUserRoleModel(user.getId(), role.getId(), role.getCode(),
					user.getUsername(), userRoles.get(i).getId(),user.getNickname()));
		}
		Long total = userRoleMapper.countByExample(userRoleExample);
		result.put("total", total);
		result.put("items", greatUserRoleModels);
		return result;
	}

	@Override
	public JSONArray deleteByExample(Long userId, JSONArray jsonArray) {
		if (!isUserExist(userId)) {
			throw new UCException(ErrorCode.UC_USERNAME_NOT_EXIST);
		}

		UserRoleExample example = new UserRoleExample();
		for (int i = 0; i < jsonArray.size(); i++) {
			example.clear();
			example.createCriteria().andUserIdEqualTo(userId).andRoleIdEqualTo(jsonArray.getLong(i));
			if (userRoleMapper.selectByExample(example).isEmpty()) {
				throw new UCException(ErrorCode.UC_ROLE_NOT_EXIST);
			}
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			example.clear();
			example.createCriteria().andUserIdEqualTo(userId).andRoleIdEqualTo(jsonArray.getLong(i));
			userRoleMapper.deleteByExample(example);
		}
		// 更新缓存数据
		rolePermissionCache.refresh(userId);
		return jsonArray;
	}

	@Override
	public JSONArray deleteByPrimaryKey(JSONArray userRoleJsonArray) {
		for (int i = 0; i < userRoleJsonArray.size(); i++) {
			if (userRoleMapper.selectByPrimaryKey(userRoleJsonArray.getLong(i)) == null) {
				throw new UCException(ErrorCode.UC_USER_ROLE_NOT_EXIST);
			}
		}
		for (int i = 0; i < userRoleJsonArray.size(); i++) {
			UserRole userRole = userRoleMapper.selectByPrimaryKey(userRoleJsonArray.getLong(i));
			userRoleMapper.deleteByPrimaryKey(userRoleJsonArray.getLong(i));
			// 更新缓存数据
			rolePermissionCache.refresh(userRole.getUserId());
		}
		return userRoleJsonArray;
	}

	public boolean isUserExist(Long userId) {
		if (userMapper.selectByPrimaryKey(userId) != null)
			return true;
		return false;
	}

	public boolean isRoleExist(JSONArray jsonArray) {
		RoleExample roleExample = new RoleExample();
		for (int i = 0; i < jsonArray.size(); i++) {
			roleExample.clear();
			roleExample.createCriteria().andIdEqualTo(jsonArray.getLong(i));
			if (roleMapper.selectByExample(roleExample).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
