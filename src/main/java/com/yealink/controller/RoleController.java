package com.yealink.controller;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.CheckParameter;
import com.yealink.model.RoleForm;
import com.yealink.model.UpdateRoleForm;
import com.yealink.service.ICommonService;
import com.yealink.uc.entity.Role;
import com.yealink.uc.entity.RoleExample;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
	
	@Autowired
	private ICommonService<Role,RoleExample> commonService;
	
	/**
	 * 获取角色全量
	 * 
	 * @param request 
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 全部角色
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	public JSONObject getAllRole(HttpServletRequest request,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		RoleExample example = new RoleExample();
		example.setLimit(limit);
		example.setOffset(offset);
		RoleExample.Criteria criteria = example.createCriteria();
		String keyword = request.getParameter("keyword");
		if(!StringUtils.isEmpty(keyword)){
			RoleExample.Criteria criteriaRolename = example.createCriteria();
			if(keyword.getBytes().length == keyword.length()){       //判断是否包含汉字
				criteria.andCodeLike("%" + CheckParameter.checkSql(keyword) + "%");
			}
			criteriaRolename.andNameLike("%" + CheckParameter.checkSql(keyword) + "%");
			example.or(criteriaRolename);
		}
		
		JSONObject result = commonService.findAllByExample(example);
		return result;
	}
	
	/**
	 * 新增角色
	 * @param roleForm 角色表单
	 * @param bindingResult
	 * @return 新增的角色详情
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.POST)
	public Role insertRole(@Valid@RequestBody RoleForm roleForm, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			throw new UCException("INVALID_REQUEST",bindingResult.getAllErrors().get(0).getDefaultMessage(),HttpStatus.BAD_REQUEST);
		}
		Role role = new Role();
		role = RoleForm.toRole(roleForm, role);
		commonService.insert(role);
		return selectById(role.getId());
	}
	
	/**
	 * 根据角色id获取角色详情
	 * @param id 角色id
	 * @return 角色详情
	 */
	@RequestMapping(value = "/roles/{id}", method = RequestMethod.GET)
	public Role selectById(@PathVariable Long id){
		return commonService.selectByPrimaryKey(id);
	}
	
	/**
	 * 根据角色id修改角色
	 * @param id  角色id
	 * @param updateRoleForm 修改的角色表单
	 * @param bindingResult
	 * @return 修改后的角色详情
	 */
	@RequestMapping(value = "/roles/{id}", method = RequestMethod.PUT)
	public Role updateById(@PathVariable Long id,@Valid@RequestBody UpdateRoleForm updateRoleForm, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			throw new UCException("INVALID_REQUEST",bindingResult.getAllErrors().get(0).getDefaultMessage(),HttpStatus.BAD_REQUEST);
		}
		Role role = new Role();
		role = UpdateRoleForm.toRole(updateRoleForm, role);
		commonService.updateByExampleSelective(role, id);
		return selectById(id);
	}
	
	/**
	 * 根据角色id删除角色
	 * @param id 角色id
	 * @return 删除的角色id
	 */
	@RequestMapping(value = "/roles/{id}", method = RequestMethod.DELETE)
	public JSONObject deleteById(@PathVariable Long id){
		JSONObject result = new JSONObject();
		commonService.deleteByPrimaryKey(id);
		result.put("id", id);
		return result;
	}

}
