package com.yealink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.CheckParameter;
import com.yealink.service.IGroupPermissionService;
import com.yealink.service.IRolePermissionService;
import com.yealink.service.IUserRoleService;

@RestController
@RequestMapping("/api/v1")
public class AuthorizationManagementController {

	@Autowired
	private IUserRoleService userRoleService;
	
	@Autowired
	private IRolePermissionService rolePermissionService;
	
	@Autowired
	private IGroupPermissionService groupPermissionService;
	
	/**
	 * 获取所有的用户角色
	 * @param request
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 所有的用户角色
	 */
	@RequestMapping(value = "user_roles", method = RequestMethod.GET)
	public JSONObject getAllUserRole(@RequestParam(value = "keyword", defaultValue="")String keyword,
			@RequestParam(value = "roleCode", defaultValue="")String roleCode,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		return userRoleService.selectAllUserRole(keyword, roleCode, limit, offset);
	}
	
	/**
	 * 新增用户角色
	 * @param jsonUserRole JSONObject对象，里面包含了userId以及roleIds的数组
	 * @return 新增的用户角色
	 */
	@RequestMapping(value = "user_roles", method = RequestMethod.POST)
	public JSONObject insertUserRole(@RequestBody JSONObject jsonUserRole){
		JSONArray roleJsonArray = jsonUserRole.getJSONArray("roleIds");
		if(roleJsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		Long userId = jsonUserRole.getLong("userId");
		userRoleService.insert(userId, roleJsonArray);
		return jsonUserRole;
	}
	
	/**
	 * 批量删除用户角色
	 * @param userRoleJsonObject JSONObject对象，里面包含了需要删除的用户角色的ids数组
	 * @return 删除的用户角色id
	 */
	@RequestMapping(value = "user_roles", method = RequestMethod.DELETE)
	public JSONObject deleteUserRole(@RequestBody JSONObject userRoleJsonObject){
		JSONArray userRoleJsonArray = userRoleJsonObject.getJSONArray("ids");
		userRoleService.deleteByPrimaryKey(userRoleJsonArray);
		return userRoleJsonObject;
	}
	
	/**
	 * 获取所有的角色权限
	 * @param request
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 所有的角色权限
	 */
	@RequestMapping(value = "role_permissions", method = RequestMethod.GET)
	public JSONObject getAllRolePermission(@RequestParam(value = "permissionCode", defaultValue="")String permissionCode,
			@RequestParam(value = "roleCode", defaultValue="")String roleCode,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		return rolePermissionService.selectAllRolePermission(permissionCode, roleCode, limit, offset);
	}
	
	/**
	 * 新增角色权限
	 * @param rolePermissionJsonObject JSONObject对象，里面包含了roleId以及permissionIds的数组
	 * @return 新增的角色权限
	 */
	@RequestMapping(value = "role_permissions", method = RequestMethod.POST)
	public JSONObject insertRolePermission(@RequestBody JSONObject rolePermissionJsonObject){
		JSONArray rolePermissionJsonArray = rolePermissionJsonObject.getJSONArray("permissionIds");
		Long roleId = rolePermissionJsonObject.getLong("roleId");
		if(rolePermissionJsonArray == null || roleId == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		rolePermissionService.insert(roleId, rolePermissionJsonArray);
		return rolePermissionJsonObject;
	}
	
	/**
	 * 批量删除角色权限
	 * @param rolePermissionJsonObject JSONObject对象，里面包含了需要删除的角色权限的ids数组
	 * @return 删除的角色权限id
	 */ 
	@RequestMapping(value = "role_permissions", method = RequestMethod.DELETE)
	public JSONObject deleteRolePermission(@RequestBody JSONObject rolePermissionJsonObject){
		JSONArray rolePermissionJsonArray = rolePermissionJsonObject.getJSONArray("ids");
		if(rolePermissionJsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		rolePermissionService.deleteByPrimaryKey(rolePermissionJsonArray);
		return rolePermissionJsonObject;
	}
	
	/**
	 * 获取所有的用户组权限
	 * @param request
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 所有的用户组权限
	 */
	@RequestMapping(value = "group_permissions", method = RequestMethod.GET)
	public JSONObject getAllGroupPermission(@RequestParam(value = "permissionCode", defaultValue="")String permissionCode,
			@RequestParam(value = "groupCode", defaultValue="")String groupCode,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		return groupPermissionService.selectAllGroupPermission(permissionCode, groupCode, limit, offset);
	}
	
	/**
	 * 新增用户组权限
	 * @param rolePermissionJsonObject JSONObject对象，里面包含了roleId以及permissionIds的数组
	 * @return 新增的用户组权限
	 */
	@RequestMapping(value = "group_permissions", method = RequestMethod.POST)
	public JSONObject insertGroupPermission(@RequestBody JSONObject groupPermissionJsonObject){
		JSONArray groupPermissionJsonArray = groupPermissionJsonObject.getJSONArray("permissionIds");
		Long groupId = groupPermissionJsonObject.getLong("groupId");
		if(groupPermissionJsonArray == null || groupId == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		groupPermissionService.insert(groupId, groupPermissionJsonArray);
		return groupPermissionJsonObject;
	}
	
	/**
	 * 批量删除用户组权限
	 * @param rolePermissionJsonObject JSONObject对象，里面包含了需要删除的角色权限的ids数组
	 * @return 删除的用户组权限id
	 */ 
	@RequestMapping(value = "group_permissions", method = RequestMethod.DELETE)
	public JSONObject deleteGroupPermission(@RequestBody JSONObject groupPermissionJsonObject){
		JSONArray groupPermissionJsonArray = groupPermissionJsonObject.getJSONArray("ids");
		if(groupPermissionJsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		groupPermissionService.deleteByPrimaryKey(groupPermissionJsonArray);
		return groupPermissionJsonObject;
	}
	
}
