package com.yealink.cache.impl;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.yealink.cache.guava.GuavaAbstractLoadingCache;
import com.yealink.cache.local.ILocalCache;
import com.yealink.security.authentication.constant.TokenConstant;

@Component
public class SystemManagerCacheImpl extends GuavaAbstractLoadingCache<String, Byte> implements ILocalCache<String, Byte>{

	private SystemManagerCacheImpl() {
		setMaximumSize(1000);
		setExpireAfterWriteDuration(TokenConstant.DEFAULT_ACCESS_TOKEN_VALIDITY);
	}
	
	@Override
	public Byte get(String key) {
		try {
			return getValue(key);
		} catch (ExecutionException e) {
			logger.error("Unable to get cache data",key,e);
			return null;
		}
	}

	@Override
	public void refresh(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(String key, Byte value) {
		getCache().put(key, value);
	}

	@Override
	public void invalidate(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidateAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConcurrentMap<String, Byte> asMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Byte getIfPresent(String key){
		return getCache().getIfPresent(key);
	}

	@Override
	protected Byte fetchData(String key) {
		return (byte)1;
	}
 
}
