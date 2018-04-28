package com.yealink.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yealink.cache.guava.GuavaAbstractLoadingCache;
import com.yealink.cache.local.ILocalCache;
import com.yealink.model.GroupPermissionModel;
import com.yealink.security.authentication.constant.TokenConstant;
import com.yealink.service.IGroupPermissionService;
import com.yealink.uc.entity.UserGroupPermissionExample;

@Component
public class GroupPermissionCacheImpl  extends GuavaAbstractLoadingCache<Long, List<String>> implements ILocalCache<Long, List<String>> {

	@Autowired
	private IGroupPermissionService groupPermissionService;
	
	private GroupPermissionCacheImpl() {
		setMaximumSize(1000);
		setExpireAfterWriteDuration(TokenConstant.DEFAULT_ACCESS_TOKEN_VALIDITY);
	}
	@Override
	public List<String> get(Long key) {
		try {
			return getValue(key);
		} catch (ExecutionException e) {
			logger.error("Unable to get cache data",key,e);
			return null;
		}
	}

	@Override
	public void refresh(Long key) {
		getCache().refresh(key);
		
	}

	@Override
	public void put(Long key, List<String> value) {
		getCache().put(key, value);
		
	}

	@Override
	public void invalidate(Long key) {
		getCache().invalidate(key);
	}

	@Override
	public void invalidateAll() {
		getCache().invalidateAll();
		
	}

	@Override
	public ConcurrentMap<Long, List<String>> asMap() {
		return  getCache().asMap();
	}

	@Override
	public List<String> getIfPresent(Long key) {
		return getIfPresent(key);
	}

	@Override
	protected List<String> fetchData(Long key) {
		UserGroupPermissionExample example = new UserGroupPermissionExample();
		example.createCriteria().andGroupIdEqualTo(key);
		List<GroupPermissionModel> userGroupPermissions = groupPermissionService.selectAllByGroupId(key, example);
		List<String> result = new ArrayList<>();
		for(GroupPermissionModel userGroupPermission : userGroupPermissions){
			result.add(userGroupPermission.getCode());
		}
		return result;
	}

}
