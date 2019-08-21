/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.cache;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;

/**
 * A simple cache support level one and level two cache
 * @author Zeal 2017年6月29日
 */
public class TwoLevelCache implements Cache {
	
	private String cacheName = null;
	
	private Cache l1Cache = null;
	
	private Cache l2Cache = null;
	
	/**
	 * @param cacheName
	 * @param l1Cache Must not be null
	 * @param l2Cache Can be null
	 */
	public TwoLevelCache(String cacheName, Cache l1Cache, Cache l2Cache) {
		this.cacheName = cacheName;
		this.l1Cache = l1Cache;
		this.l2Cache = l2Cache;
	}
	

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#getName()
	 */
	@Override
	public String getName() {
		return this.cacheName;
	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#getNativeCache()
	 */
	@Override
	public Object getNativeCache() {
		Object l1NativeCache = this.l1Cache.getNativeCache();
		Object l2NativeCache = this.l2Cache == null ? null : this.l2Cache.getNativeCache();
		return new Object[] {l1NativeCache, l2NativeCache};
	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#get(java.lang.Object)
	 */
	@Override
	public ValueWrapper get(Object key) {
		
		ValueWrapper value = this.l1Cache.get(key);
		if (value == null && this.l2Cache != null) {
			value = this.l2Cache.get(key);
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#get(java.lang.Object, java.lang.Class)
	 */
	@Override
	public <T> T get(Object key, Class<T> type) {
		T value = this.l1Cache.get(key, type);
		if (value == null && this.l2Cache != null) {
			value = this.l2Cache.get(key, type);
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(Object key, Object value) {
		this.l1Cache.put(key, value);
		if (this.l2Cache != null) {
			this.l2Cache.put(key, value);
		}

	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		final ValueWrapper vw1 = this.l1Cache.putIfAbsent(key, value);
		ValueWrapper _vw2 = null;
		if (this.l2Cache != null) {
			_vw2 = this.l2Cache.putIfAbsent(key, value);
		}
		final ValueWrapper vw2 = _vw2;
		return new ValueWrapper() {
			
			@Override
			public Object get() {
				// TODO Auto-generated method stub
				return new Object[] {vw1, vw2};
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#evict(java.lang.Object)
	 */
	@Override
	public void evict(Object key) {
		this.l1Cache.evict(key);
		if (this.l2Cache != null) {
			this.l2Cache.evict(key);
		}

	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		this.l1Cache.clear();
		if (this.l2Cache != null) {
			this.l2Cache.clear();
		}

	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		ValueWrapper wrapper =  this.get(key);
		if (wrapper != null) {
			return (T) wrapper.get();
		}
		
		synchronized (this.l1Cache) {
			if (this.l2Cache != null) {
				synchronized (this.l2Cache) {
					return loadValue(key, valueLoader);
				}
			}
			else {
				return loadValue(key, valueLoader);
			}
	
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T loadValue(Object key, Callable<T> valueLoader) {
		//Double check
		ValueWrapper wrapper =  this.get(key);
		if (wrapper != null) {
			return (T) wrapper.get();
		}
		T value = null;
		try {
			value = valueLoader.call();
		}
		catch (Throwable t) {
			throw new ValueRetrievalException(key, valueLoader, t);
		}
		put(key, value);
		return value;
	}

}
