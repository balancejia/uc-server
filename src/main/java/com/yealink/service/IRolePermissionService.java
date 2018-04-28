package com.yealink.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.model.RolePermissionModel;
import com.yealink.uc.entity.RolePermissionExample;

public interface IRolePermissionService {
	JSONArray insert(Long roleId, JSONArray jsonArray);
    List<RolePermissionModel> selectAllByRoleId(Long roleId, RolePermissionExample example);
    Long countByExample(Long roleId);
    JSONArray deleteByExample(Long roleId,JSONArray jsonArray);
    JSONArray deleteByPrimaryKey(JSONArray rolePermissionJsonArray);
	JSONObject selectAllRolePermission(String permissionCode, String roleCode, Integer limit, Integer offset);
}
