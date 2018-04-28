package com.yealink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.yealink.service.IRolePermissionService;
import com.yealink.uc.entity.RolePermissionExample;

@RestController
@RequestMapping("api/v1/roles")
public class RolePermissionController {

	@Autowired
	private IRolePermissionService rolePermissionService;
	
	/**
	 * 根据角色id获取该角色下的角色权限
	 * @param roleId 角色id
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 该角色下的角色权限
	 */
	@RequestMapping(value = "/{roleId}/permissions", method = RequestMethod.GET)
	public JSONObject getAllRolePermission(@PathVariable Long roleId,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		JSONObject result = new JSONObject();
		RolePermissionExample example = new RolePermissionExample();
		example.setLimit(limit);
		example.setOffset(offset);
		example.createCriteria().andRoleIdEqualTo(roleId);
		
		Long total = rolePermissionService.countByExample(roleId);
		result.put("total", total);
		result.put("items", rolePermissionService.selectAllByRoleId(roleId, example));
		return result;
	}
	
	/**
	 * 新增角色权限
	 * @param roleId 角色id
	 * @param jsonPermissionId JSONObject对象，包含了permissionId的数组
	 * @return 新增的角色权限，权限的id
	 */
	@RequestMapping(value = "/{roleId}/permissions", method = RequestMethod.POST)
	public JSONObject insertRolePermission(@PathVariable Long roleId, @RequestBody JSONObject jsonPermissionId){
		JSONArray jsonArray = jsonPermissionId.getJSONArray("permissionIds");
		if(jsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject result = new JSONObject();
		result.put("permissionIds", rolePermissionService.insert(roleId, jsonArray));
		return result;
	}
	
	/**
	 * 批量删除角色权限
	 * @param roleId 角色id
	 * @param jsonPermissionId JSONObject对象，包含了permissionId的数组
	 * @return 删除的角色权限，权限的id
	 */
	@RequestMapping(value = "/{roleId}/permissions", method = RequestMethod.DELETE)
	public JSONObject deleteRolePermission(@PathVariable Long roleId,@RequestBody JSONObject jsonPermissionId){
		JSONArray jsonArray = jsonPermissionId.getJSONArray("permissionIds");
		if(jsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject result = new JSONObject();
		result.put("permissionIds", rolePermissionService.deleteByExample(roleId, jsonArray));
		return result;
	}
}
