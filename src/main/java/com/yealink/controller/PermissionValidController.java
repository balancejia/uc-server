package com.yealink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yealink.model.UserInfoModel;
import com.yealink.service.IUserService;

@RestController
@RequestMapping("/apis/v1")
public class PermissionValidController {
	
	@Autowired
	private IUserService userService;
	
	/**
	 * 根据用户id获取角色权限
	 * 
	 * @param userId 用户id
	 * @return 该用户的角色权限
	 */
	@RequestMapping(value = "/users/{userId}/role_permissions", method = RequestMethod.GET)	
	public JSONObject getRolePermission(@PathVariable Long userId){
		JSONObject result = userService.getRolePermission(userId);
		return result;
	}
	
	/**
	 * 根据用户id进行权限认证
	 * 
	 * @param userId 用户的id
	 * @param authsValidJSONObject POST的表单的JSONObject对象
	 * @return 如果存在该权限返回true 否则返回false true和false存放在一个JSONObject对象里面
	 */
	@RequestMapping(value = "/auths/{userId}/valid", method = RequestMethod.POST)	
	public JSONObject authsValid(@PathVariable Long userId, @RequestBody JSONObject authsValidJSONObject){
		JSONObject result = userService.authsValid(userId, authsValidJSONObject);
		
		return result;
	}
	
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)	
	public UserInfoModel selectById(@PathVariable Long userId){
		return userService.selectByUserId(userId);
	}
}
