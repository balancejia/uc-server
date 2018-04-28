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
import com.yealink.service.IUserRoleService;
import com.yealink.uc.entity.UserRoleExample;

@RestController
@RequestMapping("/api/v1/users")
public class UserRoleController {
	
	@Autowired
	private IUserRoleService userRoleService;
	
	/**
	 * 根据用户id获取用户角色
	 * @param userId 用户id
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 该用户的用户角色
	 */
	@RequestMapping(value = "/{userId}/roles", method = RequestMethod.GET)
	public JSONObject getAllUserRoleByUserId(@PathVariable Long userId,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		JSONObject result = new JSONObject();
		UserRoleExample example = new UserRoleExample();
		example.setLimit(limit);
		example.setOffset(offset);
		example.createCriteria().andUserIdEqualTo(userId);
		
		Long total = userRoleService.countByExample(userId);
		result.put("total", total);
		result.put("items", userRoleService.selectAllByUserId(userId, example));
		return result;
	}
	
	/**
	 * 新增用户角色
	 * @param userId 用户id
	 * @param jsonRoleIds JSONObject对象，包含了roleIds的数组
	 * @return 新增的用户角色，角色id
	 */
	@RequestMapping(value = "/{userId}/roles", method = RequestMethod.POST)
	public JSONObject insertUserRole(@PathVariable Long userId,@RequestBody JSONObject jsonRoleIds){
		JSONArray jsonArray = jsonRoleIds.getJSONArray("roleIds");
		if(jsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject result = new JSONObject();
		result.put("roleIds", userRoleService.insert(userId, jsonArray));
		return result;
	}

	/**
	 * 批量删除用户角色id
	 * @param userId 用户id
	 * @param jsonRoleIds JSONObject对象，包含了roleIds的数组
	 * @return 删除的用户角色，角色id
	 */
	@RequestMapping(value = "/{userId}/roles", method = RequestMethod.DELETE)
	public JSONObject deleteUserRole(@PathVariable Long userId,@RequestBody JSONObject jsonRoleIds){
		JSONArray jsonArray = jsonRoleIds.getJSONArray("roleIds");
		if(jsonArray == null){
			throw new UCException(ErrorCode.PARAMETER_ERROR);
		}
		JSONObject result = new JSONObject();
		result.put("roleIds", userRoleService.deleteByExample(userId, jsonArray));
		return result;
	}
}
