package com.yealink.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.yealink.common.util.CheckParameter;
import com.yealink.service.ICommonService;
import com.yealink.uc.entity.UserGroup;
import com.yealink.uc.entity.UserGroupExample;

@RestController
@RequestMapping("/api/v1")
public class GroupController {

	@Autowired
	private ICommonService<UserGroup, UserGroupExample> commonService;
	
	@RequestMapping(value = "/user_groups", method = RequestMethod.GET)
	public JSONObject getAllGroup(HttpServletRequest request,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		UserGroupExample groupExample = new UserGroupExample();
		UserGroupExample.Criteria criteria = groupExample.createCriteria();
		groupExample.setLimit(limit);
		groupExample.setOffset(offset);
		
		String groupName = request.getParameter("name");
		String realm = request.getParameter("realm");
		if(!StringUtils.isEmpty(groupName)){
			criteria.andNameLike("%" + CheckParameter.checkSql(groupName) + "%");
		}
		if(!StringUtils.isEmpty(realm)){
			criteria.andRealmEqualTo(CheckParameter.checkSql(realm));
		}
		return commonService.findAllByExample(groupExample);
	}
	
	@RequestMapping(value = "/user_groups/{id}", method = RequestMethod.GET)
	public UserGroup getUserGroupByid(@PathVariable Long id){
		return commonService.selectByPrimaryKey(id);
	}
	


}
