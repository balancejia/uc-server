
package com.yealink.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.model.UserRoleModel;
import com.yealink.uc.entity.UserRoleExample;


public interface IUserRoleService {

	JSONArray insert(Long userId, JSONArray jsonArray);
    List<UserRoleModel> selectAllByUserId(Long userId, UserRoleExample example);
    Long countByExample(Long userId);
    JSONArray deleteByExample(Long userId,JSONArray jsonArray);
    JSONArray deleteByPrimaryKey(JSONArray jsonArray);
	JSONObject selectAllUserRole(String keyword, String roleCode, Integer limit, Integer offset);
}
