package com.yealink.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.cache.impl.ADUserGroupCacheImpl;
import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.EncrypUtils;
import com.yealink.common.util.GroupLoginUtil;
import com.yealink.common.util.GroupSourceEnum;
import com.yealink.common.util.UserStatus;
import com.yealink.model.ADApplicationProperties;
import com.yealink.model.UserInfoModel;
import com.yealink.security.authentication.model.UCAccount;
import com.yealink.service.IUserService;
import com.yealink.uc.dao.UserGroupMapper;
import com.yealink.uc.dao.UserMapper;
import com.yealink.uc.dao.UserRoleMapper;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserExample;
import com.yealink.uc.entity.UserGroup;
import com.yealink.uc.entity.UserGroupExample;
import com.yealink.uc.entity.UserRoleExample;

@Service
public class UserServiceImpl extends CommonServiceImpl<User, UserExample> implements IUserService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private UserMapper userMapper;
	
	@Resource
	private UserRoleMapper userRoleMapper;

	@Autowired
	private ILocalCache<Long, JSONObject> rolePermissionCache;
	
	@Autowired
	private ILocalCache<Long, List<String>> groupPermissionCacheImpl;
	 
	@Autowired
	private ADApplicationProperties adApplicationProperties;
	 
	@Autowired
	private ADUserGroupCacheImpl adUserGroupCache;
	
	@Autowired  
	private Environment environment;
	 
	@Autowired
	private ADServiceImpl aDServiceImpl;
	
	@Autowired
	private UserGroupMapper userGroupMapper;
	 
	public boolean validUserPassword(UCAccount ucAccount) {
		UserExample example = new UserExample();
		example.createCriteria().andUsernameEqualTo(ucAccount.getUsername());
		example.setLimit(1);
		List<User> opsUsers = userMapper.selectByExample(example);
	
		if (opsUsers != null && opsUsers.size() > 0) {
			if(opsUsers.get(0).getStatus().equals(UserStatus.DISABLE.getStatus())){
				throw new UCException(ErrorCode.UC_USER_DISABLE);
			}
			String pass = opsUsers.get(0).getPassword();
			if(EncrypUtils.validPassword(ucAccount.getPassword(), pass)){
				ucAccount.setUserId(opsUsers.get(0).getId().toString());
				return true;
			}
		}
		return false;
	}
	
	public boolean checkAD(UCAccount ucAccount){
		String username = ucAccount.getUsername();
	    List<Map<String,String>> adName = adApplicationProperties.getAdName();
    	String host = username.split("@")[1];
    	String hostName = null;
	    for(int i = 0; i < adName.size(); i++){
	    	if(!StringUtils.isEmpty(adName.get(i).get(host))){
	    		hostName = adName.get(i).get(host);
	    		break;
	    	}
	    }
	    if(StringUtils.isEmpty(hostName)){
	    	return false;
	    }
	    String port = environment.getProperty("ad.adProperties."+ hostName +".port"); // 端口
	    Hashtable<String, Object> env = GroupLoginUtil.groupLogin(username, ucAccount.getPassword(), hostName, host, port);
	    try {
	    	LdapContext ctx = new InitialLdapContext(env, null);
	    	adUserGroupCache.getUserGroupFromAD(ctx, username, hostName);
			UserExample example = new UserExample();
			example.createCriteria().andUsernameEqualTo(ucAccount.getUsername());
			List<User> users = userMapper.selectByExample(example);
			//本地库没有该用户信息,更新用户表信息
			if(users.isEmpty() || users.size() == 0){
				aDServiceImpl.getUserFromAD(ctx, username, host, hostName);
				users = userMapper.selectByExample(example);
			}
			User user = users.get(0);
			if(user.getStatus().equals(UserStatus.DISABLE.getStatus())){
				throw new UCException(ErrorCode.UC_USER_DISABLE);
			}
			ucAccount.setUserId(user.getId().toString());
			ctx.close();
	        return true;
	    } catch (NamingException err) {
	    	throw new UCException("UNAUTHORIZED", "uc.token.unauthorized", HttpStatus.UNAUTHORIZED);
	    } 
	}
	
	
	@Override
	public List<User> selectByExample(UserExample example) {
		return userMapper.selectByExample(example);
	}
	
	@Override
	public int insert(User record) {
		record.setPassword(EncrypUtils.encryptMD5_STD(record.getPassword()));
		String username = record.getUsername();
		String realm;
		realm = username.split("@")[1];
		record.setRealm(realm);
		record.setSource(GroupSourceEnum.INNER.getSource());
		record.setCreateTime(new Date(System.currentTimeMillis()));
		try {
			return userMapper.insert(record);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_USERNAME_EXISTED);
		}
	}
	
	@Override
	public long countByExample(UserExample example) {
		return userMapper.countByExample(example);
	}
	
	@Override
	public UserInfoModel selectByUserId(Long id) {
		User user = selectByPrimaryKey(id);
		return new UserInfoModel(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getStatus(),
					user.getAvatar(), user.getCreateTime(), user.getUpdateTime(), user.getRealm());
	}
	
	@Override
	public User selectByPrimaryKey(Long id) {
		User user = userMapper.selectByPrimaryKey(id);
		if(user != null){
			return user;
		}
		throw new UCException(ErrorCode.UC_USERNAME_NOT_EXIST);
	}
	
	@Override
	public int deleteByPrimaryKey(Long id) {
		int num = userMapper.deleteByPrimaryKey(id);
		//删除用户角色
		UserRoleExample userRoleExample = new UserRoleExample();
		userRoleExample.createCriteria().andUserIdEqualTo(id);
		userRoleMapper.deleteByExample(userRoleExample);
		//更新缓存
		rolePermissionCache.invalidate(id);
		return num;
	}
	
	@Override
	public int updateByExampleSelective(User record, Long id) {
		UserExample userExample = new UserExample();
		userExample.createCriteria().andIdEqualTo(id);
		try {
			return userMapper.updateByExampleSelective(record, userExample);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_USERNAME_EXISTED);
		}
	}
	
	@Override
	public int resetPassword(Long id, String password) {
		UserExample userExample = new UserExample();
		userExample.createCriteria().andIdEqualTo(id);
		User user = new User();
		user.setPassword(EncrypUtils.encryptMD5_STD(password));
		try {
			return userMapper.updateByExampleSelective(user, userExample);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.UC_USERNAME_EXISTED);
		}
	}
	
	@Override
	public JSONObject findAllByExample(UserExample example) {
		List<User> userList = selectByExample(example);
		Long total = countByExample(example);
		List<UserInfoModel> userInfoModels = new ArrayList<>();
		for(User user : userList){
			userInfoModels.add(new UserInfoModel(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getStatus(),
					user.getAvatar(), user.getCreateTime(), user.getUpdateTime(), user.getRealm()));
		}
		JSONObject result = new JSONObject();
		result.put("total", total);
		result.put("items", userInfoModels);
		return result;
	}
	
	@Override
	public JSONObject getRolePermission(Long userId){
		selectByPrimaryKey(userId);
		JSONObject result = new JSONObject();
		JSONObject rolePermission = rolePermissionCache.get(userId);
		List<String> roleCode = (List<String>) rolePermission.get("roles");
		Set<String> permissionCode = (Set<String>) rolePermission.get("permissions");
		Set<String> resultPermissionCode = new HashSet<>();
		JSONObject userGroups = getUserGroup(userId);
		if(userGroups != null){
			JSONArray userGroupArray = userGroups.getJSONArray("userGroup");
			UserGroupExample userGroupExample = new UserGroupExample();
			for(int i = 0; i < userGroupArray.size(); i++){
				userGroupExample.clear();
				userGroupExample.createCriteria().andNameEqualTo(userGroupArray.getString(i));
				UserGroup userGroup = userGroupMapper.selectByExample(userGroupExample).get(0);
				List<String> groupPermissionCode = groupPermissionCacheImpl.get(userGroup.getId());
				resultPermissionCode.addAll(groupPermissionCode);
			}
		}
		resultPermissionCode.addAll(permissionCode);
		result.put("roles", roleCode);
		result.put("permissions", resultPermissionCode);
		return result;
	}
	
	@Override
	public JSONObject authsValid(Long userId, JSONObject authsValidJSONObject) {
		JSONArray auths = authsValidJSONObject.getJSONArray("auths");
		String express = authsValidJSONObject.getString("express");
		if(auths == null || express == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject rolePermissionResult = getRolePermission(userId);
		List<String> userRoleCode = (List<String>) rolePermissionResult.get("roles");
		Set<String> userPermissionCode = (Set<String>) rolePermissionResult.get("permissions");
		
		List<String> roleCode = new ArrayList<>();
		Set<String> permissionCode = new HashSet<>();
		for(int i = 0; i < auths.size(); i++){
			String auth = auths.getString(i);
			if (auth.startsWith("ROLE_")){
				roleCode.add(auth);
			}
			else if (auth.startsWith("P_")){
				permissionCode.add(auth);
			}else {
				throw new UCException(ErrorCode.UC_ROLE_PERMISSION_INVALID);
			}
		}
		
		JSONObject authResult = new JSONObject();
		if (express.equals("AND")) {
			if(userRoleCode.containsAll(roleCode) && userPermissionCode.containsAll(permissionCode)){
				authResult.put("authResult", "true");
			}else {
				authResult.put("authResult", "false");
			}
		}else if (express.equals("OR")) {
			for(int i = 0; i < auths.size(); i++){
				if(userPermissionCode.contains(auths.get(i))){
					authResult.put("authResult", "true");
					break;
				}
				if(userRoleCode.contains(auths.get(i))){
					authResult.put("authResult", "true");
					break;
				}
			}
			if(authResult.isEmpty()){
				authResult.put("authResult", "false");
			}
		}else {
			throw new UCException(ErrorCode.UC_EXPRESS_INVALID);
		}
		return authResult;
	}	
	
	@Override
	public ConcurrentMap<Long, JSONObject> getAllRolePermission() {
		return rolePermissionCache.asMap();
	}

	
	@Override
	public JSONObject getUserGroup(Long userId) {
		User user = selectByPrimaryKey(userId);
		String username = user.getUsername();
		JSONObject result = new JSONObject();
	    String[] usernameTmp = username.split("@");
	    List<Map<String,String>> adName = adApplicationProperties.getAdName();
    	String host = usernameTmp[usernameTmp.length-1];
    	String hostName = null;
	    for(int i = 0; i < adName.size(); i++){
	    	if(!StringUtils.isEmpty(adName.get(i).get(host))){
	    		hostName = adName.get(i).get(host);
	    		break;
	    	}
	    }
	    if(StringUtils.isEmpty(hostName)){
	    	//throw new UCException(ErrorCode.AD_NOT_EXIST);
	    	return null;
	    }
	    result.put("userGroup", adUserGroupCache.get(username));
		return result;
		
	}
	
}
