package com.yealink.service;


import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.yealink.model.UserInfoModel;
import com.yealink.security.authentication.model.UCAccount;

public interface IUserService {
	
	
	public boolean validUserPassword(UCAccount ucAccount);
	
	public boolean checkAD(UCAccount ucAccount);

    int resetPassword(Long id ,String password);

    JSONObject getRolePermission(Long userId);
    
    JSONObject authsValid (Long userId, JSONObject authsValidJSONObject);
    
    ConcurrentMap<Long, JSONObject> getAllRolePermission();
    
	UserInfoModel selectByUserId(Long id);

	JSONObject getUserGroup(Long userId);
}
