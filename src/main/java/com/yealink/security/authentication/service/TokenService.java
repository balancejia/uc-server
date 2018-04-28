package com.yealink.security.authentication.service;

import java.io.Serializable;

import org.springframework.security.core.Authentication;

import com.yealink.security.authentication.model.UCAccount;
import com.yealink.security.authentication.model.UCMacToken;

/**
 * Token 获取/验证/刷新接口
 */
public interface TokenService<T extends Serializable> {

	/**
	 * 获取token
	 * 
	 * @return
	 */
	T getToken(UCAccount ucAccount);

	/**
	 * 验证token
	 * 
	 * @param preAuthenticatedAuthentication
	 *            授权信息
	 * @return
	 */
	T verifyToken(Authentication preAuthenticatedAuthentication);

	/**
	 * 刷新token
	 * 
	 * @return
	 */
	T refreshToken(T token);
	
	/**
	 * 创建token
	 * 
	 * @return
	 */
	T createToken(UCAccount ucAccount);
}
