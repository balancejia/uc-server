package com.yealink.security.authentication.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.yealink.security.authentication.enums.TokenType;
import com.yealink.security.authentication.service.IAuthorizeInfoService;
import com.yealink.uc.dao.AuthorizeInfoMapper;
import com.yealink.uc.entity.AuthorizeInfo;
import com.yealink.uc.entity.AuthorizeInfoExample;

@Service
public class AuthorizeInfoServiceImpl implements IAuthorizeInfoService {

	@Resource
	private AuthorizeInfoMapper authorizeInfoMapper;

	@Override
	public boolean replaceMacAuthorizeRelation(AuthorizeInfo authorizeInfo) {
		AuthorizeInfoExample example = new AuthorizeInfoExample();
		AuthorizeInfoExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(authorizeInfo.getUserId()).andTokenTypeEqualTo(TokenType.MAC.name());
		List<AuthorizeInfo> authorizations = authorizeInfoMapper.selectByExample(example);
		if (CollectionUtils.isEmpty(authorizations)) {
			// 用户与APP之间不存在特定授权关系
			return authorizeInfoMapper.insertSelective(authorizeInfo) > 0;
		} else {
			// 用户与APP之间存在特定授权关系
			AuthorizeInfo ar = authorizations.get(0);
			ar.setExpiresTime(authorizeInfo.getExpiresTime());
			ar.setTokenType(TokenType.MAC.name());
			ar.setSignKey(authorizeInfo.getSignKey());
			ar.setRefreshToken(authorizeInfo.getRefreshToken());
			ar.setAccessToken(authorizeInfo.getAccessToken());
			ar.setUpdateTime(authorizeInfo.getUpdateTime());
			example = new AuthorizeInfoExample();
			criteria = example.createCriteria();
			criteria.andIdEqualTo(ar.getId()).andUserIdEqualTo(ar.getUserId());
			return authorizeInfoMapper.updateByExampleSelective(ar, example) > 0;
		}
	}

	@Override
	public AuthorizeInfo getAuthorizeInfo(String accessToken) {
		AuthorizeInfoExample example = new AuthorizeInfoExample();
		AuthorizeInfoExample.Criteria criteria = example.createCriteria();
		criteria.andAccessTokenEqualTo(accessToken);
		List<AuthorizeInfo> authorizations = authorizeInfoMapper.selectByExample(example);
		return !CollectionUtils.isEmpty(authorizations) ? authorizations.get(0) : null;
	}

	@Override
	public boolean insertAuthorizeRelation(AuthorizeInfo authorizeInfo) {
		AuthorizeInfoExample example = new AuthorizeInfoExample();
		example.createCriteria().andUserIdEqualTo(authorizeInfo.getUserId())
								.andTokenTypeEqualTo(TokenType.BEARER.toString());
		List<AuthorizeInfo> authorizations = authorizeInfoMapper.selectByExample(example);
		long currentTime = System.currentTimeMillis();
		for(int i = 0; i < authorizations.size(); i++){
			AuthorizeInfo authorization = authorizations.get(i);
			if(authorization.getExpiresTime() < currentTime){
				authorizeInfoMapper.deleteByPrimaryKey(authorization.getId());
			}
		}
		return authorizeInfoMapper.insertSelective(authorizeInfo) > 0;
	}

	@Override
	public boolean updateAuthorizeRelation(AuthorizeInfo updateInfo) {
		AuthorizeInfoExample example = new AuthorizeInfoExample();
		AuthorizeInfoExample.Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(updateInfo.getId());
		return authorizeInfoMapper.updateByExampleSelective(updateInfo, example) > 0;
	}
}
