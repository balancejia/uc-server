package com.yealink.cache.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * K : key的类型
 * V ： value的类型
 * 
 * 抽象Guava缓存类、缓存模板。
 * 子类需要实现fetchData(key)，从数据库或其他数据源（如Redis）中获取数据。
 * 子类调用getValue(key)方法，从缓存中获取数据
 * 
 */
public abstract class GuavaAbstractLoadingCache <K,V>{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private int maximumSize;					//最大缓存条数，子类在构造方法中调用setMaximumSize来更改
	private long expireAfterWriteDuration;		//数据存在时长，子类在构造方法中调用setExpireAfterWriteDuration来更改
	private TimeUnit timeUnit = TimeUnit.SECONDS;	//时间单位（秒）
	
	
	private LoadingCache<K, V> cache;
	
	public LoadingCache<K, V> getCache(){
		if(cache == null){
			synchronized(this){
				if(cache == null){
					cache = CacheBuilder.newBuilder()
							.maximumSize(maximumSize)
							.expireAfterWrite(expireAfterWriteDuration, timeUnit)
//							.recordStats()		//用来开启Guava Cache的统计功能。统计打开后，Cache.stats()方法会返回CacheStats对象以提供统计信息
							.build(new CacheLoader<K, V>(){
								@Override
								public V load(K key) throws Exception {
								return fetchData(key);
								}
							});
					logger.debug("Local cache initialization successful", this.getClass().getSimpleName());
				}
			}
		}
		return cache;
	}
	
	 /**
     * 根据key从数据库或其他数据源中获取一个value，并被自动保存到缓存中。   
     *  value,连同key一起被加载到缓存中的。    
     */    
    protected abstract V fetchData(K key); 
    
    protected V getValue(K key) throws ExecutionException {
		V result = getCache().get(key);
		return result;
	}
    
	public int getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	public long getExpireAfterWriteDuration() {
		return expireAfterWriteDuration;
	}

	public void setExpireAfterWriteDuration(long expireAfterWriteDuration) {
		this.expireAfterWriteDuration = expireAfterWriteDuration;
	}
}
