package com.yealink.service;

import java.util.Set;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

public interface IADService {

	void getAllGroupFromAD();

	void getAllUserFromAD();

	/**
	 * 根据用户组该获取用户
	 * @param groupCode 用户组编码
	 * @param host 
	 * @return
	 */
	Set<String> getUsernameByGroup(String groupCode, String host);

	void getUserFromAD(LdapContext ctx, String username, String host, String hostName) throws NamingException;

}
