package com.yealink.security.authentication.service;

import java.util.List;

import com.yealink.security.authentication.model.UCMacToken;
import com.yealink.security.authentication.model.UCPermissionsDetails;
import com.yealink.security.authentication.model.UCRoleDetails;
import com.yealink.security.authentication.model.UCUserDetails;
import com.yealink.security.authentication.model.UserInfo;

/**
 * 获取用户详细信息的服务接口。
 *
 */
public interface IUserDetailsService {

	UCUserDetails loadUserDetailsByUserId(String userId);

	UserInfo getUserInfo(String userId);

	List<UCRoleDetails> getUserRoleList(String userId);

	UCUserDetails loadUserDetailsByUserId(UCMacToken safUcToken);

	List<UCPermissionsDetails> getUserPermissionList(String userId);
}
