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
import com.yealink.service.IGroupPermissionService;
import com.yealink.uc.entity.UserGroupPermissionExample;

@RestController
@RequestMapping("api/v1/user_groups")
public class GroupPermissionController {

	@Autowired
	private IGroupPermissionService groupPermissionService;

	/**
	 * 根据用户组id获取该用户组下的权限
	 * @param groupId 用户组id
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 该用户组的权限
	 */
	@RequestMapping(value = "/{groupId}/permissions", method = RequestMethod.GET)
	public JSONObject getAllGroupPermission(@PathVariable Long groupId,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		JSONObject result = new JSONObject();
		UserGroupPermissionExample example = new UserGroupPermissionExample();
		example.setLimit(limit);
		example.setOffset(offset);
		example.createCriteria().andGroupIdEqualTo(groupId);
		
		Long total = groupPermissionService.countByExample(groupId);
		result.put("total", total);
		result.put("items", groupPermissionService.selectAllByGroupId(groupId, example));
		return result;
	}
	
	/**
	 * 新增用户组权限
	 * @param groupId 用户组id
	 * @param jsonPermissionId JSONObject对象，包含了permissionId的数组
	 * @return 新增的用户组权限，权限的id
	 */
	@RequestMapping(value = "/{groupId}/permissions", method = RequestMethod.POST)
	public JSONObject insertGroupPermission(@PathVariable Long groupId, @RequestBody JSONObject jsonPermissionId){
		JSONArray jsonArray = jsonPermissionId.getJSONArray("permissionIds");
		if(jsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject result = new JSONObject();
		result.put("permissionIds", groupPermissionService.insert(groupId, jsonArray));
		return result;
	}
	
	/**
	 * 批量删除用户组权限
	 * @param groupId 用户组id
	 * @param jsonPermissionId JSONObject对象，包含了permissionId的数组
	 * @return 删除的用户组权限，权限的id
	 */
	@RequestMapping(value = "/{groupId}/permissions", method = RequestMethod.DELETE)
	public JSONObject deleteGroupPermission(@PathVariable Long groupId,@RequestBody JSONObject jsonPermissionId){
		JSONArray jsonArray = jsonPermissionId.getJSONArray("permissionIds");
		if(jsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject result = new JSONObject();
		result.put("permissionIds", groupPermissionService.deleteByExample(groupId, jsonArray));
		return result;
	}
}
