package com.yealink.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.CheckParameter;
import com.yealink.model.PermissionForm;
import com.yealink.model.UpdatePermissionForm;
import com.yealink.service.ICommonService;
import com.yealink.uc.entity.Permission;
import com.yealink.uc.entity.PermissionExample;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

	@Autowired
	private ICommonService<Permission, PermissionExample> commonService;

	/**
	 * 获取所有的权限
	 * @param request
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @param keyword 编码或名称
	 * @return 所有的权限
	 */
	@RequestMapping(value = "/permissions", method = RequestMethod.GET)
	public JSONObject getAllPermission(HttpServletRequest request,
			@RequestParam(value = "$limit", defaultValue = "10") Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0") Integer offset,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {
		CheckParameter.checkLimitAndOffset(limit, offset);
		PermissionExample example = new PermissionExample();

		example.setLimit(limit);
		example.setOffset(offset);
		if (StringUtils.isNotEmpty(keyword)) {
			PermissionExample.Criteria codeCriteria = example.createCriteria();
			PermissionExample.Criteria nameCriteria = example.createCriteria();
			codeCriteria.andCodeLike("%" + CheckParameter.checkSql(keyword) + "%");
			nameCriteria.andNameLike("%" + CheckParameter.checkSql(keyword) + "%");
			example.or(nameCriteria);
		}
		JSONObject result = commonService.findAllByExample(example);
		return result;
	}

	/**
	 * 新增权限
	 * @param permissionForm 权限的表单
	 * @param bindingResult
	 * @return 新增的权限
	 */
	@RequestMapping(value = "/permissions", method = RequestMethod.POST)
	public Permission insertPermission(@Valid @RequestBody PermissionForm permissionForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		Permission permission = new Permission();
		permission = PermissionForm.toPermission(permissionForm, permission);
		commonService.insert(permission);
		return selectById(permission.getId());
	}

	/**
	 * 根据权限id获取权限详情
	 * @param id 权限id
	 * @return 权限详情
	 */
	@RequestMapping(value = "/permissions/{id}", method = RequestMethod.GET)
	public Permission selectById(@PathVariable Long id) {
		return commonService.selectByPrimaryKey(id);
	}

	/**
	 * 修改权限
	 * @param id 权限id
	 * @param updatePermissionForm 修改权限的表单
	 * @param bindingResult
	 * @return 修改后的权限详情
	 */
	@RequestMapping(value = "/permissions/{id}", method = RequestMethod.PUT)
	public Permission updateById(@PathVariable Long id, @Valid @RequestBody UpdatePermissionForm updatePermissionForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		Permission permission = new Permission();
		permission = UpdatePermissionForm.toPermission(updatePermissionForm, permission);
		commonService.updateByExampleSelective(permission, id);
		return selectById(id);
	}

	/**
	 * 根据id删除权限
	 * @param id 权限id
	 * @return 删除的权限的id
	 */
	@RequestMapping(value = "/permissions/{id}", method = RequestMethod.DELETE)
	public JSONObject deleteById(@PathVariable Long id) {
		JSONObject result = new JSONObject();
		commonService.deleteByPrimaryKey(id);
		result.put("id", id);
		return result;
	}
}
