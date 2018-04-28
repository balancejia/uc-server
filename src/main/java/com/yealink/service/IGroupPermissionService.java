package com.yealink.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yealink.model.GroupPermissionModel;
import com.yealink.uc.entity.UserGroupPermissionExample;

public interface IGroupPermissionService {

	JSONArray insert(Long groupId, JSONArray jsonArray);
    List<GroupPermissionModel> selectAllByGroupId(Long groupId, UserGroupPermissionExample example);
    Long countByExample(Long groupId);
    JSONArray deleteByExample(Long groupId,JSONArray jsonArray);
    JSONArray deleteByPrimaryKey(JSONArray groupPermissionJsonArray);
	JSONObject selectAllGroupPermission(String permissionCode, String groupCode, Integer limit, Integer offset);
}
