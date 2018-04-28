package com.yealink.security.authentication.service;

import com.yealink.uc.entity.AuthorizeInfo;

public interface IAuthorizeInfoService {
	
    public boolean replaceMacAuthorizeRelation(AuthorizeInfo authorizeInfo);

	public AuthorizeInfo getAuthorizeInfo(String accessToken);
	
	public boolean insertAuthorizeRelation(AuthorizeInfo authorizeInfo);

	public boolean updateAuthorizeRelation(AuthorizeInfo updateInfo);

}
