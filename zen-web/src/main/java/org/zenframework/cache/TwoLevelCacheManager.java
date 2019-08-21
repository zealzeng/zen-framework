/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

/**
 * A simple cache manager which supports level one and level two cache
 * @author Zeal 2017年6月29日
 */
public class TwoLevelCacheManager implements CacheManager, InitializingBean {
	
	//Level 1 cache manager
	private CacheManager l1CacheManager = null;
	
	//Level 2 cache manager
	private CacheManager l2CacheManager = null;
	
	public TwoLevelCacheManager() {
	}
	
	public TwoLevelCacheManager(CacheManager l1, CacheManager l2) {
		this.l1CacheManager = l1;
		this.l2CacheManager = l2;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.l1CacheManager, "L1 cache manager is null");
	}

	/**
	 * @return the l1CacheManager
	 */
	public CacheManager getL1CacheManager() {
		return l1CacheManager;
	}

	/**
	 * @param l1CacheManager the l1CacheManager to set
	 */
	public void setL1CacheManager(CacheManager l1CacheManager) {
		this.l1CacheManager = l1CacheManager;
	}

	/**
	 * @return the l2CacheManager
	 */
	public CacheManager getL2CacheManager() {
		return l2CacheManager;
	}

	/**
	 * @param l2CacheManager the l2CacheManager to set
	 */
	public void setL2CacheManager(CacheManager l2CacheManager) {
		this.l2CacheManager = l2CacheManager;
	}

	@Override
	public Cache getCache(String name) {
		Cache l1Cache = this.l1CacheManager.getCache(name);
		Cache l2Cache = this.l2CacheManager == null ? null : this.l2CacheManager.getCache(name);
		return new TwoLevelCache(name, l1Cache, l2Cache);
	}

	@Override
	public Collection<String> getCacheNames() {
		Set<String> names = new HashSet<>();
		names.addAll(this.l1CacheManager.getCacheNames());
		if (this.l2CacheManager != null) {
			names.addAll(this.l2CacheManager.getCacheNames());
		}
		return Collections.unmodifiableSet(names);
	}
	
	

}
