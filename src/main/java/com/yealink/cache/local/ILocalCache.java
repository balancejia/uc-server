package com.yealink.cache.local;

import java.util.concurrent.ConcurrentMap;

public interface ILocalCache<K,V> {

	/**
	 * 通过key获取value，如果缓存中key不存在，则根据key加载数据进缓存
	 *  K : key的类型
	 *  V ： value的类型
	 * @param key
	 * @return value
	 */
	V get (K key);
	
	 /**
	  *  K : key的类型
	  *  刷新缓存
	  * @param key
	  */
    void refresh(K key);
    
    /**
     * 设置缓存数据
     */
    void put(K key, V value);
    
    /**
     * 根据key清除缓存
     */
    void invalidate(K key);

    /**
     * 清除所有缓存
     */
    void invalidateAll();
    
    /**
     * 获取缓存的所有数据
     */
    ConcurrentMap<K, V> asMap();
    
    /**
     * 获取缓存数据，如果缓存中key不存在，返回null，不会加载数据进缓存
     * @param key
     * @return
     */
    V getIfPresent(K key);
}
